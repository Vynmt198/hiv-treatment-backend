package com.hivmedical.medical.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.entitty.Appointment;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.Role;
import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.repository.AppointmentRepository;
import com.hivmedical.medical.repository.DoctorRepository;
import com.hivmedical.medical.repository.ScheduleRepository;
import com.hivmedical.medical.repository.UserRepositoty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
  private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
  private final AppointmentRepository appointmentRepository;
  private final UserRepositoty userRepository;
  private final DoctorRepository doctorRepository;
  private final ScheduleRepository scheduleRepository;
  private final ObjectMapper objectMapper;
  private final EmailService emailService;

  public AppointmentService(AppointmentRepository appointmentRepository,
      UserRepositoty userRepository,
      DoctorRepository doctorRepository,
      ScheduleRepository scheduleRepository,
      ObjectMapper objectMapper,
      EmailService emailService) {
    this.appointmentRepository = appointmentRepository;
    this.userRepository = userRepository;
    this.doctorRepository = doctorRepository;
    this.scheduleRepository = scheduleRepository;
    this.objectMapper = objectMapper;
    this.emailService = emailService;
  }

  public AppointmentDTO createAppointment(AppointmentDTO request) {
    logger.info("Creating appointment with request: {}", request);

    // Validate doctor
    Doctor doctor = doctorRepository.findById(request.getDoctorId())
        .orElseThrow(() -> {
          logger.error("Doctor not found for ID: {}", request.getDoctorId());
          return new RuntimeException("Doctor not found with ID: " + request.getDoctorId());
        });

    // Validate user (if not anonymous)
    UserEntity user = null;
    if (!request.isAnonymous() && request.getUserId() != null) {
      user = userRepository.findById(request.getUserId())
          .orElseThrow(() -> {
            logger.error("User not found for ID: {}", request.getUserId());
            return new RuntimeException("User not found with ID: " + request.getUserId());
          });
      if (!user.getRole().equals(Role.PATIENT)) {
        logger.error("User ID {} is not a PATIENT", request.getUserId());
        throw new RuntimeException("Only PATIENT users can book non-anonymous appointments");
      }
    }

    // Validate appointment type
    if (!"EXPLORATION".equals(request.getType()) && !"CONSULTATION".equals(request.getType())) {
      logger.error("Invalid appointment type: {}", request.getType());
      throw new RuntimeException("Invalid appointment type: " + request.getType());
    }

    // Validate schedule availability
    List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDate(request.getDoctorId(), request.getDate());
    logger.debug("Found {} schedules for doctor ID {} on date {}", schedules.size(), request.getDoctorId(), request.getDate());
    boolean isAvailable = false;
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
    for (Schedule schedule : schedules) {
      try {
        List<String> timeSlots = objectMapper.readValue(schedule.getTimeSlots(), new TypeReference<List<String>>() {});
        logger.debug("Available time slots for doctor ID {} on {}: {}", request.getDoctorId(), request.getDate(), timeSlots);
        for (String slot : timeSlots) {
          String[] times = slot.split("-");
          if (times.length != 2) {
            logger.error("Invalid time slot format: {}", slot);
            continue;
          }
          LocalTime startTime = LocalTime.parse(times[0] + ":00", timeFormatter);
          LocalTime endTime = LocalTime.parse(times[1] + ":00", timeFormatter);
          LocalTime requestedTime = request.getTime();
          logger.debug("Checking time slot {} - {} for requested time {}", startTime, endTime, requestedTime);
          if (!requestedTime.isBefore(startTime) && !requestedTime.isAfter(endTime)) {
            isAvailable = true;
            break;
          }
        }
        if (isAvailable) break;
      } catch (Exception e) {
        logger.error("Error parsing time slots for schedule ID: {}", schedule.getId(), e);
        throw new RuntimeException("Error parsing time slots: " + e.getMessage());
      }
    }
    if (!isAvailable) {
      logger.error("Time slot {} not available for doctor ID: {} on date: {}",
          request.getTime(), request.getDoctorId(), request.getDate());
      throw new RuntimeException("Selected time slot is not available");
    }

    // Check for conflicting appointments
    logger.debug("Checking for conflicting appointments: doctorId={}, date={}, time={}",
        request.getDoctorId(), request.getDate(), request.getTime());
    if (appointmentRepository.existsByDoctorIdAndDateAndTime(request.getDoctorId(), request.getDate(), request.getTime())) {
      logger.error("Time slot {} already booked for doctor ID: {} on date: {}",
          request.getTime(), request.getDoctorId(), request.getDate());
      throw new RuntimeException("Time slot is already booked");
    }

    // Create appointment
    Appointment appointment = new Appointment();
    appointment.setUser(user);
    appointment.setDoctor(doctor);
    appointment.setDate(request.getDate());
    appointment.setTime(request.getTime());
    appointment.setType(request.getType());
    appointment.setAnonymous(request.isAnonymous());
    appointment.setStatus("PENDING");
    if (request.isAnonymous()) {
      appointment.setReferenceCode(UUID.randomUUID().toString());
    }

    appointment = appointmentRepository.save(appointment);
    logger.info("Appointment created successfully: {}", appointment);

    // Send email notification
    try {
      String email = request.isAnonymous() ? doctor.getEmail() : user.getEmail();
      emailService.sendAppointmentConfirmation(email, appointment.getReferenceCode(),
          request.getDate(), request.getTime(), request.getType());
      logger.info("Confirmation email sent to {} for appointment {}", email, appointment.getId());
    } catch (Exception e) {
      logger.error("Failed to send confirmation email for appointment {}: {}", appointment.getId(), e.getMessage());
    }

    return convertToDTO(appointment);
  }

  public AppointmentDTO createAppointmentWithUsername(String username, AppointmentDTO request) {
    logger.info("Creating appointment for username: {}", username);
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          logger.error("User not found with username: {}", username);
          return new RuntimeException("User not found with username: " + username);
        });
    request.setUserId(user.getUserId());
    return createAppointment(request);
  }

  public List<AppointmentDTO> getAppointmentsByUsername(String username) {
    logger.info("Retrieving appointments for username: {}", username);
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          logger.error("User not found with username: {}", username);
          return new RuntimeException("User not found with username: " + username);
        });
    return getUserAppointments(user.getUserId());
  }

  public List<AppointmentDTO> getUserAppointments(Long userId) {
    logger.info("Retrieving appointments for user ID: {}", userId);
    userRepository.findById(userId)
        .orElseThrow(() -> {
          logger.error("User not found for ID: {}", userId);
          return new RuntimeException("User not found with ID: " + userId);
        });
    List<Appointment> appointments = appointmentRepository.findByUserId(userId);
    return appointments.stream().map(this::convertToDTO).collect(java.util.stream.Collectors.toList());
  }

  private AppointmentDTO convertToDTO(Appointment appointment) {
    AppointmentDTO dto = new AppointmentDTO();
    dto.setId(appointment.getId());
    dto.setUserId(appointment.getUser() != null ? appointment.getUser().getUserId() : null);
    dto.setDoctorId(appointment.getDoctor().getId());
    dto.setDate(appointment.getDate());
    dto.setTime(appointment.getTime());
    dto.setType(appointment.getType());
    dto.setAnonymous(appointment.isAnonymous());
    dto.setStatus(appointment.getStatus());
    dto.setReferenceCode(appointment.getReferenceCode());
    return dto;
  }
}