package com.hivmedical.medical.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivmedical.medical.dto.DoctorDTO;
import com.hivmedical.medical.dto.ScheduleDTO;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.repository.DoctorRepository;
import com.hivmedical.medical.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
  private final DoctorRepository doctorRepository;
  private final ScheduleRepository scheduleRepository;
  private final ObjectMapper objectMapper;

  private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

  public List<Doctor> getAllDoctors() {
    return doctorRepository.findAll();
  }

  public DoctorService(DoctorRepository doctorRepository, ScheduleRepository scheduleRepository,
                       ObjectMapper objectMapper) {
    this.doctorRepository = doctorRepository;
    this.scheduleRepository = scheduleRepository;
    this.objectMapper = objectMapper;
  }
  //create
  public DoctorDTO createDoctor(DoctorDTO dto) {
    if (doctorRepository.findByEmail(dto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email đã tồn tại: " + dto.getEmail());
    }
    Doctor doctor = new Doctor();
    doctor.setFullName(dto.getFullName());
    doctor.setSpecialization(dto.getSpecialization());
    doctor.setQualification(dto.getQualification());
    doctor.setEmail(dto.getEmail());
    doctor.setPhoneNumber(dto.getPhoneNumber());
    doctor.setWorkingSchedule(dto.getWorkingSchedule());
    doctor.setImageUrl(dto.getImageUrl());
    Doctor savedDoctor = doctorRepository.save(doctor);
    return convertToDTO(savedDoctor);
  }

  public Page<DoctorDTO> getDoctors(String search, String searchBy, Pageable pageable) {
    Page<Doctor> doctors;
    if (search != null && !search.isEmpty()) {
      if ("name".equalsIgnoreCase(searchBy)) {
        doctors = doctorRepository.findByNameContainingIgnoreCase(search, pageable);
      } else {
        doctors = doctorRepository.findBySpecializationContainingIgnoreCase(search, pageable);
      }
    } else {
      doctors = doctorRepository.findAll(pageable);
    }
    return doctors.map(this::convertToDTO);
  }

  public DoctorDTO getDoctorById(Long id) {
    Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bác sĩ với ID " + id + " không tồn tại"));
    return convertToDTO(doctor);
  }
  //update
  public DoctorDTO updateDoctor(Long id, DoctorDTO dto) {
    Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bác sĩ với ID " + id + " không tồn tại"));
    if (doctorRepository.findByEmail(dto.getEmail()).isPresent() && !doctor.getEmail().equals(dto.getEmail())) {
      throw new IllegalArgumentException("Email đã tồn tại: " + dto.getEmail());
    }
    doctor.setFullName(dto.getFullName());
    doctor.setSpecialization(dto.getSpecialization());
    doctor.setQualification(dto.getQualification());
    doctor.setEmail(dto.getEmail());
    doctor.setPhoneNumber(dto.getPhoneNumber());
    doctor.setWorkingSchedule(dto.getWorkingSchedule());
    doctor.setImageUrl(dto.getImageUrl());
    Doctor updatedDoctor = doctorRepository.save(doctor);
    return convertToDTO(updatedDoctor);
  }
  // Delete
  public void deleteDoctor(Long id) {
    if (!doctorRepository.existsById(id)) {
      throw new RuntimeException("Bác sĩ với ID " + id + " không tồn tại");
    }
    doctorRepository.deleteById(id);
  }

  private DoctorDTO convertToDTO(Doctor doctor) {
    DoctorDTO dto = new DoctorDTO();
    dto.setId(doctor.getId());
    dto.setFullName(doctor.getFullName());
    dto.setSpecialization(doctor.getSpecialization());
    dto.setQualification(doctor.getQualification());
    dto.setEmail(doctor.getEmail());
    dto.setPhoneNumber(doctor.getPhoneNumber());
    dto.setWorkingSchedule(doctor.getWorkingSchedule());
    dto.setImageUrl(doctor.getImageUrl());
    return dto;
  }

  private ScheduleDTO convertToScheduleDTO(Schedule schedule) {
    ScheduleDTO dto = new ScheduleDTO();
    dto.setId(schedule.getId());
    dto.setDoctorId(schedule.getDoctor().getId());
    dto.setDate(schedule.getDate() != null ? schedule.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
    try {
      List<String> timeSlots = objectMapper.readValue(schedule.getTimeSlots(), new TypeReference<List<String>>() {
      });
      dto.setTimeSlots(timeSlots);
    } catch (Exception e) {
      throw new RuntimeException("Error parsing time slots", e);
    }
    dto.setStartTime(
            schedule.getStartTime() != null ? schedule.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
    dto.setEndTime(
            schedule.getEndTime() != null ? schedule.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
    dto.setCreatedAt(
            schedule.getCreatedAt() != null ? schedule.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
    dto.setUpdatedAt(
            schedule.getUpdatedAt() != null ? schedule.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
    dto.setAvailable(schedule.isAvailable());
    return dto;
  }

  public List<ScheduleDTO> getDoctorSchedule(Long id, String date) {
    logger.debug("Fetching schedules for doctorId: {}, date: {}", id, date);
    LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
    List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDate(id, localDate);
    logger.debug("Found {} schedules in database for doctorId: {}", schedules.size(), id);
    return schedules.stream()
            .filter(schedule -> {
              if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
                logger.warn("Schedule {} has null startTime or endTime", schedule.getId());
                return false;
              }
              return schedule.isAvailable();
            })
            .map(schedule -> {
              ScheduleDTO dto = new ScheduleDTO();
              dto.setId(schedule.getId());
              dto.setDoctorId(id);
              dto.setDate(
                      schedule.getDate() != null ? schedule.getDate().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
                              : null);
              dto.setTimeSlots(null); // Can be parsed if needed
              dto.setStartTime(schedule.getStartTime() != null
                      ? schedule.getStartTime().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                      : null);
              dto.setEndTime(schedule.getEndTime() != null
                      ? schedule.getEndTime().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                      : null);
              dto.setAvailable(schedule.isAvailable());
              dto.setCreatedAt(schedule.getCreatedAt() != null
                      ? schedule.getCreatedAt().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                      : null);
              dto.setUpdatedAt(schedule.getUpdatedAt() != null
                      ? schedule.getUpdatedAt().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                      : null);
              return dto;
            })
            .collect(Collectors.toList());
  }

  public List<ScheduleDTO> getAvailableDoctorSchedules(Long doctorId, String date) {
    LocalDate localDate = LocalDate.parse(date);
    List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDateAndIsAvailableTrue(doctorId, localDate);
    return schedules.stream().map(this::convertToScheduleDTO).toList();
  }

  public void syncWorkScheduleToSchedule(Long doctorId, int daysInFuture) {
    Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
    // Giả sử workschedule là: "Thứ 2-6: 08:00-17:00"
    // Parse ra các ngày, giờ bắt đầu/kết thúc
    // (Bạn có thể dùng regex hoặc lưu workschedule dạng JSON để dễ parse hơn)
    for (int i = 0; i < daysInFuture; i++) {
      LocalDate date = LocalDate.now().plusDays(i);
      LocalTime start = LocalTime.of(8, 0);
      LocalTime end = LocalTime.of(17, 0);
      while (start.isBefore(end)) {
        LocalTime slotEnd = start.plusMinutes(30);
        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);
        schedule.setDate(date);
        schedule.setStartTime(LocalDateTime.of(date, start));
        schedule.setEndTime(LocalDateTime.of(date, slotEnd));
        schedule.setAvailable(true);
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());
        // ... set các trường khác nếu cần
        scheduleRepository.save(schedule);
        start = slotEnd;
      }
    }
  }

  public List<DoctorDTO> getAvailableDoctorsBySlot(String date, String startTime, String endTime,
      String specialization) {
    LocalDate localDate = LocalDate.parse(date);
    java.time.LocalDateTime start = java.time.LocalDateTime.parse(date + "T" + startTime);
    java.time.LocalDateTime end = java.time.LocalDateTime.parse(date + "T" + endTime);
    List<Schedule> availableSchedules = scheduleRepository.findByDateAndStartTimeAndEndTimeAndIsAvailableTrue(localDate,
        start, end);
    return availableSchedules.stream()
        .map(Schedule::getDoctor)
        .filter(doctor -> specialization == null || specialization.isEmpty()
            || (doctor.getSpecialization() != null && doctor.getSpecialization().equalsIgnoreCase(specialization)))
        .distinct()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }
}