package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.entitty.AppointmentEntity;
import com.hivmedical.medical.entitty.ServiceEntity;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.repository.AppointmentRepository;
import com.hivmedical.medical.repository.ServiceRepository;
import com.hivmedical.medical.repository.UserRepositoty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

  @Autowired
  private AppointmentRepository appointmentRepository;

  @Autowired
  private UserRepositoty userRepository;

  @Autowired
  private ServiceRepository serviceRepository;

  public AppointmentDTO createAppointment(AppointmentDTO dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại: " + username));
    ServiceEntity service = serviceRepository.findById(dto.getServiceId())
        .orElseThrow(() -> new RuntimeException("Dịch vụ với ID " + dto.getServiceId() + " không tồn tại"));
    if (!dto.getAppointmentType().equals("FIRST_VISIT") && !dto.getAppointmentType().equals("FOLLOW_UP")) {
      throw new IllegalArgumentException("Loại lịch khám phải là FIRST_VISIT hoặc FOLLOW_UP");
    }
    if (!service.getType().equals(dto.getAppointmentType())) {
      throw new IllegalArgumentException("Loại dịch vụ không khớp với loại lịch khám");
    }
    AppointmentEntity entity = new AppointmentEntity();
    entity.setUser(user);
    entity.setService(service);
    entity.setAppointmentType(dto.getAppointmentType());
    if (dto.getAppointmentDate() != null) {
      try {
        entity.setAppointmentDate(LocalDateTime.parse(dto.getAppointmentDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      } catch (Exception e) {
        throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ");
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

  public List<AppointmentDTO> getAllAppointments() {
    return appointmentRepository.findAll().stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  private AppointmentDTO mapToDTO(AppointmentEntity entity) {
    AppointmentDTO dto = new AppointmentDTO();
    dto.setId(entity.getId());
    dto.setServiceId(entity.getService().getId());
    dto.setServiceName(entity.getService().getName());
    dto.setAppointmentType(entity.getAppointmentType());
    dto.setUserUsername(entity.getUser().getUsername());
    if (entity.getAppointmentDate() != null) {
      dto.setAppointmentDate(entity.getAppointmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    dto.setStatus(entity.getStatus());
    return dto;
  }
}