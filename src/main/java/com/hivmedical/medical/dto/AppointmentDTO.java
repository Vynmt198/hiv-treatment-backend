package com.hivmedical.medical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentDTO {
  private Long id;

  @NotNull(message = "Service ID is required")
  private Long serviceId;

  @NotBlank(message = "Appointment type is required")
  private String appointmentType; // FIRST_VISIT hoặc FOLLOW_UP

  private String appointmentDate; // ISO 8601 format (e.g., "2025-07-01T10:00:00")

  private String status;

  private String userUsername; // Username của người dùng

  private String serviceName; // Tên dịch vụ

  public AppointmentDTO() {
  }

  public AppointmentDTO(Long id, Long serviceId, String appointmentType, String appointmentDate,
      String status, String userUsername, String serviceName) {
    this.id = id;
    this.serviceId = serviceId;
    this.appointmentType = appointmentType;
    this.appointmentDate = appointmentDate;
    this.status = status;
    this.userUsername = userUsername;
    this.serviceName = serviceName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getAppointmentType() {
    return appointmentType;
  }

  public void setAppointmentType(String appointmentType) {
    this.appointmentType = appointmentType;
  }

  public String getAppointmentDate() {
    return appointmentDate;
  }

  public void setAppointmentDate(String appointmentDate) {
    this.appointmentDate = appointmentDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUserUsername() {
    return userUsername;
  }

  public void setUserUsername(String userUsername) {
    this.userUsername = userUsername;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }
}