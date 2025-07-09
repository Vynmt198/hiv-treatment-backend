package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.OnlineAppointmentDTO;
import com.hivmedical.medical.dto.AnonymousOnlineDTO;
import com.hivmedical.medical.entitty.AppointmentEntity;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.entitty.ServiceEntity;
import com.hivmedical.medical.entitty.Account;
import com.hivmedical.medical.repository.AppointmentRepository;
import com.hivmedical.medical.repository.DoctorRepository;
import com.hivmedical.medical.repository.ServiceRepository;
import com.hivmedical.medical.repository.AccountRepository;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import com.hivmedical.medical.entitty.PatientProfile;
import com.hivmedical.medical.repository.PatientProfileRepository;
import com.hivmedical.medical.repository.ScheduleRepository;
import com.hivmedical.medical.entitty.AppointmentStatus;

@Service
public class AppointmentService {

  @Autowired
  private AppointmentRepository appointmentRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private ServiceRepository serviceRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private ScheduleService scheduleService;

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Autowired
  private PatientProfileRepository patientProfileRepository;

  @Transactional
  public AppointmentDTO createAppointment(AppointmentDTO dto) {
    // Validate input
    if (dto.getAppointmentDate() == null || dto.getAppointmentDate().isEmpty()) {
      throw new IllegalArgumentException("Ngày khám không được để trống");
    }
    if (dto.getFullName() == null || dto.getFullName().isEmpty()) {
      throw new IllegalArgumentException("Họ tên không được để trống");
    }
    if (dto.getPhone() != null && !dto.getPhone().matches("^[0-9]{9,15}$")) {
      throw new IllegalArgumentException("Số điện thoại không hợp lệ");
    }
    // Check duplicate appointment for user at the same time
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Account user = accountRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại: " + username));
    List<AppointmentEntity> existing = appointmentRepository.findByUserUsername(username);
    if (existing.stream().anyMatch(a -> a.getAppointmentDate() != null
        && a.getAppointmentDate().toString().equals(dto.getAppointmentDate()) && !a.getStatus().equals("CANCELLED"))) {
      throw new IllegalArgumentException("Bạn đã có lịch khám vào thời gian này");
    }
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

        final LocalDateTime finalAppointmentDate = parsedAppointmentDate;
        List<Schedule> availableSchedules = scheduleService.getAvailableSchedules(dto.getDoctorId(),
            parsedAppointmentDate.toLocalDate());
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
      } catch (DateTimeParseException e) {
        throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ: " + dto.getAppointmentDate());
      }
    }

    entity.setStatus(AppointmentStatus.valueOf(dto.getStatus()));
    AppointmentEntity saved = appointmentRepository.save(entity);
    PatientProfile profile = patientProfileRepository.findByAccount(user).orElse(null);
    if (profile == null) {
      profile = new PatientProfile();
      profile.setAccount(user);
      profile.setFullName(dto.getFullName());
      if (dto.getBirthDate() != null) {
        profile.setBirthDate(LocalDate.parse(dto.getBirthDate()));
      } else {
        profile.setBirthDate(LocalDate.now());
      }
      profile.setTreatmentStartDate(LocalDate.now());
      patientProfileRepository.save(profile);
    }
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

  @Transactional
  public AppointmentDTO createOnlineAppointment(OnlineAppointmentDTO dto) {
    // Validate input
    if (dto.getEmail() == null || !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      throw new IllegalArgumentException("Email không hợp lệ");
    }
    if (dto.getFullName() == null && dto.getAliasName() == null) {
      throw new IllegalArgumentException("Cần nhập họ tên hoặc bí danh");
    }
    if (dto.getPhone() != null && !dto.getPhone().matches("^[0-9]{9,15}$")) {
      throw new IllegalArgumentException("Số điện thoại không hợp lệ");
    }
    // Check duplicate online appointment for email at the same time
    List<AppointmentEntity> existing = appointmentRepository.findByUserEmail(dto.getEmail());
    if (existing.stream().anyMatch(
        a -> a.getAppointmentDate() != null && a.getAppointmentDate().toLocalDate().toString().equals(dto.getDate())
            && !a.getStatus().equals("CANCELLED"))) {
      throw new IllegalArgumentException("Bạn đã có lịch online vào ngày này");
    }
    // Tìm bác sĩ chuyên khoa Chuyên khoa HIV/AIDS còn slot trống vào ngày
    // dto.getDate()
    // Ưu tiên slot đầu tiên còn trống
    LocalDate date = LocalDate.parse(dto.getDate());
    List<Doctor> doctors = doctorRepository.findBySpecializationContainingIgnoreCase("Chuyên khoa HIV/AIDS");
    for (Doctor doctor : doctors) {
      List<Schedule> schedules = scheduleService.getAvailableSchedules(doctor.getId(), date);
      for (Schedule schedule : schedules) {
        if (schedule.isAvailable()) {
          // Tạo user nếu chưa có
          Account user = accountRepository.findByEmail(dto.getEmail()).orElseGet(() -> {
            Account newUser = new Account();
            newUser.setEmail(dto.getEmail());
            newUser.setUsername(dto.getEmail());
            newUser.setPasswordHash("");
            newUser.setRole(com.hivmedical.medical.entitty.Role.PATIENT);
            newUser.setEnabled(true);
            return accountRepository.save(newUser);
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
          entity.setStatus(AppointmentStatus.PENDING);
          entity.setCreatedAt(LocalDateTime.now());
          entity.setUpdatedAt(LocalDateTime.now());
          entity.setPhone(dto.getPhone());
          entity.setGender(dto.getGender());
          entity.setDescription(dto.getDescription());
          AppointmentEntity saved = appointmentRepository.save(entity);
          scheduleService.markScheduleAsBooked(schedule.getId());
          PatientProfile profile = patientProfileRepository.findByAccount(user).orElse(null);
          if (profile == null) {
            profile = new PatientProfile();
            profile.setAccount(user);
            profile.setFullName(dto.getFullName() != null ? dto.getFullName() : dto.getAliasName());
            if (dto.getBirthDate() != null && !dto.getBirthDate().isEmpty()) {
              profile.setBirthDate(LocalDate.parse(dto.getBirthDate()));
            } else {
              profile.setBirthDate(LocalDate.now());
            }
            profile.setTreatmentStartDate(LocalDate.now());
            patientProfileRepository.save(profile);
          }
          return mapToDTO(saved);
        }
      }
    }
    throw new IllegalArgumentException("Không còn slot trống cho ngày đã chọn");
  }

  @Transactional
  public AppointmentDTO createAnonymousOnlineAppointment(AnonymousOnlineDTO dto) {
    // Validate input
    if (dto.getAliasName() == null || dto.getAliasName().isEmpty()) {
      throw new IllegalArgumentException("Cần nhập bí danh");
    }
    if (dto.getPhone() != null && !dto.getPhone().matches("^[0-9]{9,15}$")) {
      throw new IllegalArgumentException("Số điện thoại không hợp lệ");
    }
    // Tìm bác sĩ chuyên khoa Chuyên khoa HIV/AIDS còn slot trống (ưu tiên slot đầu
    // tiên)
    LocalDate date = dto.getDate();
    List<Doctor> doctors = doctorRepository.findBySpecializationContainingIgnoreCase("Chuyên khoa HIV/AIDS");
    for (Doctor doctor : doctors) {
      List<Schedule> schedules = scheduleService.getAvailableSchedules(doctor.getId(), date);
      for (Schedule schedule : schedules) {
        if (schedule.isAvailable()) {
          // Tạo user ẩn danh tạm thời
          Account user = new Account();
          user.setEmail("anonymous_" + System.currentTimeMillis() + "@anonymous.com");
          user.setUsername("anonymous_" + System.currentTimeMillis());
          user.setPasswordHash("");
          user.setRole(com.hivmedical.medical.entitty.Role.PATIENT);
          user.setEnabled(true);
          user = accountRepository.save(user);
          // Tạo appointment
          AppointmentEntity entity = new AppointmentEntity();
          entity.setUser(user);
          ServiceEntity service = serviceRepository.findByName("Tư vấn online HIV")
              .orElseThrow(() -> new RuntimeException("Service not found"));
          entity.setService(service);
          entity.setDoctor(doctor);
          entity.setAppointmentType("FIRST_VISIT");
          entity.setAppointmentDate(schedule.getStartTime());
          entity.setStatus(AppointmentStatus.PENDING);
          entity.setCreatedAt(LocalDateTime.now());
          entity.setUpdatedAt(LocalDateTime.now());
          entity.setPhone(dto.getPhone());
          entity.setGender(dto.getGender());
          entity.setDescription(dto.getDescription());
          AppointmentEntity saved = appointmentRepository.save(entity);
          scheduleService.markScheduleAsBooked(schedule.getId());
          PatientProfile profile = patientProfileRepository.findByAccount(user).orElse(null);
          if (profile == null) {
            profile = new PatientProfile();
            profile.setAccount(user);
            profile.setFullName(dto.getAliasName());
            if (dto.getBirthDate() != null && !dto.getBirthDate().isEmpty()) {
              profile.setBirthDate(LocalDate.parse(dto.getBirthDate()));
            } else {
              profile.setBirthDate(LocalDate.now());
            }
            profile.setTreatmentStartDate(LocalDate.now());
            patientProfileRepository.save(profile);
          }
          return mapToDTO(saved);
        }
      }
    }
    throw new IllegalArgumentException("Không còn slot trống cho ngày đã chọn");
  }

  @Transactional
  public AppointmentDTO updateAppointmentStatus(Long id, String statusStr) {
    AppointmentEntity entity = appointmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn với id: " + id));
    if (statusStr == null || statusStr.isEmpty()) {
      throw new IllegalArgumentException("Trạng thái không hợp lệ");
    }
    AppointmentStatus newStatus;
    try {
      newStatus = AppointmentStatus.valueOf(statusStr);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Trạng thái không hợp lệ: " + statusStr);
    }
    // Kiểm soát luồng chuyển đổi trạng thái hợp lệ
    AppointmentStatus current = entity.getStatus();
    if (!isValidStatusTransition(current, newStatus)) {
      throw new IllegalArgumentException("Không thể chuyển trạng thái từ " + current + " sang " + newStatus);
    }
    entity.setStatus(newStatus);
    AppointmentEntity saved = appointmentRepository.save(entity);
    return mapToDTO(saved);
  }

  private boolean isValidStatusTransition(AppointmentStatus current, AppointmentStatus next) {
    switch (current) {
      case PENDING:
        return next == AppointmentStatus.CHECKED_IN || next == AppointmentStatus.ABSENT;
      case CHECKED_IN:
        return next == AppointmentStatus.IN_PROGRESS || next == AppointmentStatus.ABSENT;
      case IN_PROGRESS:
        return next == AppointmentStatus.COMPLETED || next == AppointmentStatus.ABSENT;
      case COMPLETED:
      case ABSENT:
        return false;
      default:
        return false;
    }
  }

  public List<AppointmentDTO> getAppointmentsByStatus(String statusStr) {
    AppointmentStatus status;
    try {
      status = AppointmentStatus.valueOf(statusStr);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Trạng thái không hợp lệ: " + statusStr);
    }
    return appointmentRepository.findByStatus(status).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
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
    dto.setStatus(entity.getStatus().name());
    dto.setPhone(entity.getPhone());
    dto.setGender(entity.getGender());
    dto.setDescription(entity.getDescription());
    PatientProfile profile = patientProfileRepository.findByAccount(entity.getUser()).orElse(null);
    String fullName = (profile != null) ? profile.getFullName() : null;
    dto.setFullName(fullName);
    return dto;
  }

  public List<Schedule> getAvailableSchedules(Long doctorId, LocalDateTime startTime) {
    return scheduleRepository.findByDoctorIdAndIsAvailableTrueAndStartTimeAfter(doctorId, startTime);
  }
}