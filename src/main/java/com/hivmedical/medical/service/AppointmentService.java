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
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AppointmentService {

  private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

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
    entity.setFullName(dto.getFullName());
    entity.setBookingMode(AppointmentEntity.BookingMode.NORMAL);

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
        // Giữ slot ở trạng thái PENDING, chờ thanh toán
        scheduleService.holdScheduleForBooking(bookedSchedule.getId());
      } catch (DateTimeParseException e) {
        throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ: " + dto.getAppointmentDate());
      }
    }

    entity.setStatus(AppointmentStatus.PENDING);
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
        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  // New method to get all appointments
  public List<AppointmentDTO> getAllAppointments() {
    return appointmentRepository.findAll().stream()
        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
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
    if (dto.getServiceId() == null) {
      throw new IllegalArgumentException("Cần chọn dịch vụ");
    }
    if (dto.getDoctorId() == null) {
      throw new IllegalArgumentException("Cần chọn bác sĩ");
    }
    if (dto.getAppointmentDate() == null || dto.getAppointmentDate().isEmpty()) {
      throw new IllegalArgumentException("Cần chọn thời gian lịch hẹn");
    }
    // Kiểm tra slot
    ServiceEntity service = serviceRepository.findById(dto.getServiceId())
        .orElseThrow(() -> new RuntimeException("Service not found"));
    Doctor doctor = doctorRepository.findById(dto.getDoctorId())
        .orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại"));
    LocalDateTime parsedAppointmentDate;
    try {
      parsedAppointmentDate = LocalDateTime.parse(dto.getAppointmentDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ: " + dto.getAppointmentDate());
    }
    List<Schedule> availableSchedules = scheduleService.getAvailableSchedules(dto.getDoctorId(),
        parsedAppointmentDate.toLocalDate());
    boolean isAvailable = availableSchedules.stream()
        .anyMatch(schedule -> schedule.getStartTime().equals(parsedAppointmentDate));
    if (!isAvailable) {
      throw new IllegalArgumentException("Khung giờ này không còn trống");
    }
    Schedule bookedSchedule = availableSchedules.stream()
        .filter(schedule -> schedule.getStartTime().equals(parsedAppointmentDate))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ không hợp lệ"));
    if (!bookedSchedule.isAvailable()) {
      throw new IllegalArgumentException("Khung giờ này đã có người đặt");
    }
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
    entity.setService(service);
    entity.setDoctor(doctor);
    entity.setAppointmentType("FIRST_VISIT");
    entity.setAppointmentDate(parsedAppointmentDate);
    entity.setStatus(AppointmentStatus.ONLINE_PENDING);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());
    entity.setPhone(dto.getPhone());
    entity.setGender(dto.getGender());
    entity.setDescription(dto.getDescription());
    entity.setBookingMode(AppointmentEntity.BookingMode.ONLINE);
    AppointmentEntity saved = appointmentRepository.save(entity);
    scheduleService.markScheduleAsBooked(bookedSchedule.getId());
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

  @Transactional
  public AppointmentDTO createAnonymousOnlineAppointment(AnonymousOnlineDTO dto) {
    // Validate input
    if (dto.getAliasName() == null || dto.getAliasName().isEmpty()) {
      throw new IllegalArgumentException("Cần nhập bí danh");
    }
    if (dto.getPhone() != null && !dto.getPhone().matches("^[0-9]{9,15}$")) {
      throw new IllegalArgumentException("Số điện thoại không hợp lệ");
    }
    if (dto.getServiceId() == null) {
      throw new IllegalArgumentException("Cần chọn dịch vụ");
    }
    if (dto.getDoctorId() == null) {
      throw new IllegalArgumentException("Cần chọn bác sĩ");
    }
    if (dto.getAppointmentDate() == null || dto.getAppointmentDate().isEmpty()) {
      throw new IllegalArgumentException("Cần chọn thời gian lịch hẹn");
    }
    ServiceEntity service = serviceRepository.findById(dto.getServiceId())
        .orElseThrow(() -> new RuntimeException("Service not found"));
    Doctor doctor = doctorRepository.findById(dto.getDoctorId())
        .orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại"));
    LocalDateTime parsedAppointmentDate;
    try {
      parsedAppointmentDate = LocalDateTime.parse(dto.getAppointmentDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ: " + dto.getAppointmentDate());
    }
    List<Schedule> availableSchedules = scheduleService.getAvailableSchedules(dto.getDoctorId(),
        parsedAppointmentDate.toLocalDate());
    boolean isAvailable = availableSchedules.stream()
        .anyMatch(schedule -> schedule.getStartTime().equals(parsedAppointmentDate));
    if (!isAvailable) {
      throw new IllegalArgumentException("Khung giờ này không còn trống");
    }
    Schedule bookedSchedule = availableSchedules.stream()
        .filter(schedule -> schedule.getStartTime().equals(parsedAppointmentDate))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ không hợp lệ"));
    if (!bookedSchedule.isAvailable()) {
      throw new IllegalArgumentException("Khung giờ này đã có người đặt");
    }
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
    entity.setService(service);
    entity.setDoctor(doctor);
    entity.setAppointmentType("FIRST_VISIT");
    entity.setAppointmentDate(parsedAppointmentDate);
    entity.setStatus(AppointmentStatus.ONLINE_ANONYMOUS_PENDING);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());
    entity.setPhone(dto.getPhone());
    entity.setGender(dto.getGender());
    entity.setDescription(dto.getDescription());
    entity.setBookingMode(AppointmentEntity.BookingMode.ANONYMOUS_ONLINE);
    AppointmentEntity saved = appointmentRepository.save(entity);
    scheduleService.markScheduleAsBooked(bookedSchedule.getId());
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
    dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
    dto.setPhone(entity.getPhone());
    dto.setGender(entity.getGender());
    dto.setDescription(entity.getDescription());
    // Ưu tiên lấy fullName từ entity, nếu null mới lấy từ profile
    if (entity.getFullName() != null && !entity.getFullName().isEmpty()) {
      dto.setFullName(entity.getFullName());
    } else {
      PatientProfile profile = patientProfileRepository.findByAccount(entity.getUser()).orElse(null);
      String fullName = (profile != null) ? profile.getFullName() : null;
      dto.setFullName(fullName);
    }
    dto.setBookingMode(entity.getBookingMode() != null ? entity.getBookingMode().name() : null);
    dto.setPrice(entity.getService().getPrice());
    return dto;
  }

  public List<Schedule> getAvailableSchedules(Long doctorId, LocalDateTime startTime) {
    return scheduleRepository.findByDoctorIdAndIsAvailableTrueAndStartTimeAfter(doctorId, startTime);
  }

  // Thêm hàm nhận AppointmentStatus cho updateAppointmentStatus
  @Transactional
  public AppointmentDTO updateAppointmentStatus(Long id, AppointmentStatus status) {
    AppointmentEntity entity = appointmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Lịch hẹn không tồn tại với id: " + id));
    entity.setStatus(status);
    entity.setUpdatedAt(LocalDateTime.now());
    AppointmentEntity saved = appointmentRepository.save(entity);
    return mapToDTO(saved);
  }

  // Thêm hàm nhận AppointmentStatus cho getAppointmentsByStatus
  public List<AppointmentDTO> getAppointmentsByStatus(AppointmentStatus status) {
    return appointmentRepository.findByStatus(status).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
    return appointmentRepository.findByUserId(patientId).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public AppointmentDTO confirmPayment(Long appointmentId) {
    AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    switch (appointment.getBookingMode()) {
      case NORMAL:
        appointment.setStatus(AppointmentStatus.CHECKED_IN); // hoặc IN_PROGRESS nếu muốn
        break;
      case ONLINE:
        appointment.setStatus(AppointmentStatus.IN_PROGRESS); // hoặc thêm ONLINE_PAID nếu muốn
        break;
      case ANONYMOUS_ONLINE:
        appointment.setStatus(AppointmentStatus.IN_PROGRESS); // hoặc thêm ONLINE_ANONYMOUS_PAID nếu muốn
        break;
    }
    appointment.setUpdatedAt(LocalDateTime.now());
    appointmentRepository.save(appointment);
    // Nếu cần, cập nhật schedule sang BOOKED (nếu có liên kết)
    // scheduleService.confirmScheduleBooking(appointment.getScheduleId());
    return mapToDTO(appointment);
  }

  public ServiceEntity getServiceById(Long id) {
    return serviceRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Service not found"));
  }

  @Scheduled(fixedRate = 600000) // mỗi 10 phút
  public void cleanUpUnpaidAppointments() {
    LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);
    List<AppointmentEntity> staleAppointments = appointmentRepository.findByStatusInAndCreatedAtBefore(
        List.of(AppointmentStatus.PENDING, AppointmentStatus.ONLINE_PENDING,
            AppointmentStatus.ONLINE_ANONYMOUS_PENDING),
        threshold);
    for (AppointmentEntity appointment : staleAppointments) {
      appointment.setStatus(AppointmentStatus.CANCELLED);
      appointment.setUpdatedAt(LocalDateTime.now());
    }
    appointmentRepository.saveAll(staleAppointments);
    if (!staleAppointments.isEmpty()) {
      logger.info("Auto-cancelled {} unpaid appointments (older than 15 minutes)", staleAppointments.size());
    }
  }
}
