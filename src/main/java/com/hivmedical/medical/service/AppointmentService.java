package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.OnlineAppointmentDTO;
import com.hivmedical.medical.dto.AnonymousOnlineDTO;
import com.hivmedical.medical.entitty.AppointmentEntity;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.entitty.ServiceEntity;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.repository.AppointmentRepository;
import com.hivmedical.medical.repository.DoctorRepository;
import com.hivmedical.medical.repository.ServiceRepository;
import com.hivmedical.medical.repository.UserRepositoty;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
public class AppointmentService {

  @Autowired
  private AppointmentRepository appointmentRepository;

  @Autowired
  private UserRepositoty userRepository;

  @Autowired
  private ServiceRepository serviceRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private ScheduleService scheduleService;

  public AppointmentDTO createAppointment(AppointmentDTO dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại: " + username));
    ServiceEntity service = serviceRepository.findById(dto.getServiceId())
        .orElseThrow(() -> new RuntimeException("Service not found"));
    Doctor doctor = doctorRepository.findById(dto.getDoctorId())
        .orElseThrow(() -> new IllegalArgumentException("Bác sĩ với ID " + dto.getDoctorId() + " không tồn tại"));
    if (!dto.getAppointmentType().equals("FIRST_VISIT") && !dto.getAppointmentType().equals("FOLLOW_UP")) {
      throw new IllegalArgumentException("Loại lịch khám phải là FIRST_VISIT hoặc FOLLOW_UP");
    }
    if (!service.getType().equals(dto.getAppointmentType())) {
      throw new IllegalArgumentException("Loại dịch vụ không khớp với loại lịch khám");
    }

    AppointmentEntity entity = new AppointmentEntity();
    entity.setUser(user);
    entity.setService(service);
    entity.setDoctor(doctor);
    entity.setAppointmentType(dto.getAppointmentType());

    if (dto.getAppointmentDate() != null) {
      LocalDateTime parsedAppointmentDate;
      try {
        try {
          // Try ISO_LOCAL_DATE_TIME (e.g., "2025-07-05T09:00:00")
          parsedAppointmentDate = LocalDateTime.parse(dto.getAppointmentDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e1) {
          try {
            // Fallback to custom format (e.g., "2025-07-05 09:00:00" or "2025-07-05
            // 09:00:00.000000")
            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]");
            parsedAppointmentDate = LocalDateTime.parse(dto.getAppointmentDate(), customFormatter);
          } catch (DateTimeParseException e2) {
            throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ: " + dto.getAppointmentDate());
          }
        }
        entity.setAppointmentDate(parsedAppointmentDate);

        final LocalDateTime finalAppointmentDate = parsedAppointmentDate; // Effectively final
        List<Schedule> availableSchedules = scheduleService.getAvailableSchedules(dto.getDoctorId(),
            LocalDateTime.now());
        boolean isAvailable = availableSchedules.stream()
            .anyMatch(schedule -> schedule.getStartTime().equals(finalAppointmentDate));
        if (!isAvailable) {
          throw new IllegalArgumentException("Khung giờ này không còn trống");
        }

        Schedule bookedSchedule = availableSchedules.stream()
            .filter(schedule -> schedule.getStartTime().equals(finalAppointmentDate))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Khung giờ không hợp lệ"));
        if (!bookedSchedule.isAvailable()) {
          throw new IllegalArgumentException("Khung giờ này đã có người đặt");
        }
        bookedSchedule.setAvailable(false);
        scheduleService.markScheduleAsBooked(bookedSchedule.getId());
      } catch (Exception e) {
        throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ: " + e.getMessage());
      }
    }

    entity.setStatus("PENDING");
    AppointmentEntity saved = appointmentRepository.save(entity);
    return mapToDTO(saved);
  }

  public List<AppointmentDTO> getUserAppointments() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return appointmentRepository.findByUserUsername(username).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  // New method to get all appointments
  public List<AppointmentDTO> getAllAppointments() {
    return appointmentRepository.findAll().stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  public AppointmentDTO createOnlineAppointment(OnlineAppointmentDTO dto) {
    // Tìm bác sĩ chuyên khoa HIV/AIDS còn slot trống vào ngày dto.getDate()
    // Ưu tiên slot đầu tiên còn trống
    LocalDate date = LocalDate.parse(dto.getDate());
    List<Doctor> doctors = doctorRepository.findBySpecializationContainingIgnoreCase("HIV/AIDS");
    for (Doctor doctor : doctors) {
      List<Schedule> schedules = scheduleService.getAvailableSchedules(doctor.getId(), date);
      for (Schedule schedule : schedules) {
        if (schedule.isAvailable()) {
          // Tạo user nếu chưa có
          UserEntity user = userRepository.findByEmail(dto.getEmail()).orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setFullName(dto.getFullName());
            newUser.setEmail(dto.getEmail());
            newUser.setUsername(dto.getEmail());
            newUser.setPasswordHash("");
            newUser.setRole(com.hivmedical.medical.entitty.Role.PATIENT);
            newUser.setEnabled(true);
            return userRepository.save(newUser);
          });
          // Tạo appointment
          AppointmentEntity entity = new AppointmentEntity();
          entity.setUser(user);
          ServiceEntity service = serviceRepository.findByName("Tư vấn online HIV")
              .orElseThrow(() -> new RuntimeException("Service not found"));
          entity.setService(service);
          entity.setDoctor(doctor);
          entity.setAppointmentType("FIRST_VISIT");
          entity.setAppointmentDate(schedule.getStartTime());
          entity.setStatus("ONLINE_PENDING");
          entity.setCreatedAt(LocalDateTime.now());
          entity.setUpdatedAt(LocalDateTime.now());
          entity.setPhone(dto.getPhone());
          entity.setGender(dto.getGender());
          entity.setDescription(dto.getDescription());
          AppointmentEntity saved = appointmentRepository.save(entity);
          scheduleService.markScheduleAsBooked(schedule.getId());
          return mapToDTO(saved);
        }
      }
    }
    throw new RuntimeException("Không còn slot trống cho ngày đã chọn");
  }

  public AppointmentDTO createAnonymousOnlineAppointment(AnonymousOnlineDTO dto) {
    // Tìm bác sĩ chuyên khoa HIV/AIDS còn slot trống (ưu tiên slot đầu tiên)
    LocalDate date = dto.getDate();
    List<Doctor> doctors = doctorRepository.findBySpecializationContainingIgnoreCase("HIV/AIDS");
    for (Doctor doctor : doctors) {
      List<Schedule> schedules = scheduleService.getAvailableSchedules(doctor.getId(), date);
      for (Schedule schedule : schedules) {
        if (schedule.isAvailable()) {
          // Tạo user ẩn danh tạm thời
          UserEntity user = new UserEntity();
          user.setFullName(dto.getAliasName());
          user.setEmail("anonymous_" + System.currentTimeMillis() + "@anonymous.com");
          user.setUsername("anonymous_" + System.currentTimeMillis());
          user.setPasswordHash("");
          user.setRole(com.hivmedical.medical.entitty.Role.PATIENT);
          user.setEnabled(true);
          user = userRepository.save(user);
          // Tạo appointment
          AppointmentEntity entity = new AppointmentEntity();
          entity.setUser(user);
          ServiceEntity service = serviceRepository.findByName("Tư vấn online HIV")
              .orElseThrow(() -> new RuntimeException("Service not found"));
          entity.setService(service);
          entity.setDoctor(doctor);
          entity.setAppointmentType("FIRST_VISIT");
          entity.setAppointmentDate(schedule.getStartTime());
          entity.setStatus("ONLINE_ANONYMOUS_PENDING");
          entity.setCreatedAt(LocalDateTime.now());
          entity.setUpdatedAt(LocalDateTime.now());
          entity.setPhone(dto.getPhone());
          entity.setGender(dto.getGender());
          entity.setDescription(dto.getDescription());
          AppointmentEntity saved = appointmentRepository.save(entity);
          scheduleService.markScheduleAsBooked(schedule.getId());
          return mapToDTO(saved);
        }
      }
    }
    throw new RuntimeException("Không còn slot trống cho ngày đã chọn");
  }

  private AppointmentDTO mapToDTO(AppointmentEntity entity) {
    AppointmentDTO dto = new AppointmentDTO();
    dto.setId(entity.getId());
    dto.setServiceId(entity.getService().getId());
    dto.setServiceName(entity.getService().getName());
    dto.setDoctorId(entity.getDoctor() != null ? entity.getDoctor().getId() : null);
    dto.setDoctorName(entity.getDoctor() != null ? entity.getDoctor().getFullName() : null);
    if (entity.getDoctor() != null) {
      dto.setSpecialization(entity.getDoctor().getSpecialization());
      dto.setDoctorEmail(entity.getDoctor().getEmail());
      dto.setDoctorPhone(entity.getDoctor().getPhoneNumber());
    }
    dto.setAppointmentType(entity.getAppointmentType());
    dto.setUserUsername(entity.getUser().getUsername());
    if (entity.getAppointmentDate() != null) {
      dto.setAppointmentDate(entity.getAppointmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    dto.setStatus(entity.getStatus());
    dto.setPhone(entity.getPhone());
    dto.setGender(entity.getGender());
    dto.setDescription(entity.getDescription());
    return dto;
  }
}