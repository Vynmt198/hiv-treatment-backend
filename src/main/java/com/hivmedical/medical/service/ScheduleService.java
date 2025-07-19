package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.AppointmentEntity;
import com.hivmedical.medical.entitty.AppointmentStatus;
import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.repository.AppointmentRepository;
import com.hivmedical.medical.repository.ScheduleRepository;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hivmedical.medical.entitty.Schedule.Status;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class ScheduleService {

  @Autowired
  private ScheduleRepository scheduleRepository;
  @Autowired
  private AppointmentRepository appointmentRepository;

  public List<Schedule> getAvailableSchedules(Long doctorId, LocalDateTime startTime) {
    return scheduleRepository.findByDoctorIdAndIsAvailableTrueAndStartTimeAfter(doctorId, startTime);
  }

  public void markScheduleAsBooked(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ với ID " + scheduleId + " không tồn tại"));
    schedule.setAvailable(false);

    schedule.setStatus(Status.BOOKED);

    scheduleRepository.save(schedule);
  }

  public List<Schedule> getAvailableSchedules(Long doctorId, LocalDate date) {
    return scheduleRepository.findByDoctorIdAndDateAndIsAvailableTrue(doctorId, date);
  }

  // Đặt lịch: chuyển slot sang PENDING, giữ chỗ 5 phút
  public void holdScheduleForBooking(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ với ID " + scheduleId + " không tồn tại"));
    if (schedule.getStatus() != Status.AVAILABLE) {
      System.out.println("Slot " + scheduleId + " không khả dụng, status hiện tại: " + schedule.getStatus());
      throw new IllegalStateException("Slot này không khả dụng");
    }
    schedule.setStatus(Status.PENDING);
    schedule.setAvailable(false);
    schedule.setPendingUntil(LocalDateTime.now().plusMinutes(5));
    scheduleRepository.save(schedule);
    System.out.println("Slot " + scheduleId + " đã được giữ chỗ thành công!");
  }

  // Xác nhận thanh toán thành công: chuyển slot sang BOOKED
  public void confirmScheduleBooking(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ với ID " + scheduleId + " không tồn tại"));
    AppointmentEntity appointmentEntity = appointmentRepository.findByScheduleId(scheduleId);
    if (appointmentEntity == null) {
      throw new IllegalArgumentException("Appointment ID không tồn tại");

    }
    if (appointmentEntity.getStatus() != AppointmentStatus.IN_PROGRESS) {
      throw new IllegalStateException("Slot này không ở trạng thái chờ thanh toán");
    }

    schedule.setStatus(Status.BOOKED);
    schedule.setAvailable(false);
    schedule.setPendingUntil(null);
    scheduleRepository.save(schedule);
    appointmentEntity.setStatus(AppointmentStatus.BOOKED);
    appointmentRepository.save(appointmentEntity);
  }

  // Scheduled job: giải phóng slot PENDING hết hạn giữ chỗ
  @Scheduled(fixedRate = 60000) // mỗi phút
  public void releaseExpiredPendingSchedules() {
    List<Schedule> expired = scheduleRepository.findByStatusAndPendingUntilBefore(Status.PENDING, LocalDateTime.now());
    for (Schedule s : expired) {
      s.setStatus(Status.AVAILABLE);
      s.setAvailable(true);
      s.setPendingUntil(null);
      scheduleRepository.save(s);
    }
  }

  public Schedule getScheduleById(Long id) {
    return scheduleRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ với ID " + id + " không tồn tại"));
  }

  // Lấy lịch làm việc của bác sĩ theo ID
  public List<com.hivmedical.medical.dto.ScheduleDTO> getDoctorSchedules(Long doctorId) {
    List<Schedule> schedules = scheduleRepository.findByDoctorIdAndIsAvailableTrueAndStartTimeAfter(doctorId,
        LocalDateTime.now());
    return schedules.stream()
        .map(this::convertToScheduleDTO)
        .collect(java.util.stream.Collectors.toList());
  }

  // Lấy lịch làm việc của bác sĩ theo ngày
  public List<com.hivmedical.medical.dto.ScheduleDTO> getDoctorSchedulesByDate(Long doctorId, LocalDate date) {
    List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDateAndIsAvailableTrue(doctorId, date);
    return schedules.stream()
        .map(this::convertToScheduleDTO)
        .collect(java.util.stream.Collectors.toList());
  }

  // Convert Schedule entity to ScheduleDTO
  private com.hivmedical.medical.dto.ScheduleDTO convertToScheduleDTO(Schedule schedule) {
    com.hivmedical.medical.dto.ScheduleDTO dto = new com.hivmedical.medical.dto.ScheduleDTO();
    dto.setId(schedule.getId());
    dto.setDoctorId(schedule.getDoctor().getId());
    dto.setDate(schedule.getDate() != null ? schedule.getDate().toString() : null);

    // Parse time slots if needed
    if (schedule.getTimeSlots() != null) {
      try {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        java.util.List<String> timeSlots = mapper.readValue(schedule.getTimeSlots(),
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {
            });
        dto.setTimeSlots(timeSlots);
      } catch (Exception e) {
        dto.setTimeSlots(java.util.List.of(schedule.getTimeSlots()));
      }
    }

    dto.setStartTime(schedule.getStartTime() != null ? schedule.getStartTime().toString() : null);
    dto.setEndTime(schedule.getEndTime() != null ? schedule.getEndTime().toString() : null);
    dto.setAvailable(schedule.isAvailable());
    dto.setCreatedAt(schedule.getCreatedAt() != null ? schedule.getCreatedAt().toString() : null);
    dto.setUpdatedAt(schedule.getUpdatedAt() != null ? schedule.getUpdatedAt().toString() : null);

    // Add doctor information
    if (schedule.getDoctor() != null) {
      dto.setDoctorName(schedule.getDoctor().getFullName());
      dto.setDoctorEmail(schedule.getDoctor().getEmail());
      dto.setDoctorPhone(schedule.getDoctor().getPhoneNumber());
      dto.setDoctorSpecialization(schedule.getDoctor().getSpecialization());
    }

    return dto;
  }
}