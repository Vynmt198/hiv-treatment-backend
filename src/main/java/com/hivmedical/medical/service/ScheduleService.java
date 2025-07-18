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
}