package com.hivmedical.medical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentDTO {
  private Long id;

  @NotNull(message = "Service ID is required")
  private Long serviceId;

  @NotNull(message = "Doctor ID is required")
  private Long doctorId;

  @NotBlank(message = "Appointment type is required")
  private String appointmentType; // FIRST_VISIT hoặc FOLLOW_UP

  private String appointmentDate; // ISO 8601 format (e.g., "2025-07-01T10:00:00")

  private String status;

  private String userUsername; // Username của người dùng

  private String serviceName; // Tên dịch vụ
  private String doctorName;

  private String specialization;
  private String doctorEmail;
  private String doctorPhone;

  private String phone;
  private String gender;
  private String description;

  private String fullName;

  private String aliasName;

  private String birthDate;

  public AppointmentDTO() {
  }

  public AppointmentDTO(Long id, Long serviceId, Long doctorId, String appointmentType,
      String appointmentDate, String status, String userUsername, String serviceName,
      String doctorName) {
    this.id = id;
    this.serviceId = serviceId;
    this.doctorId = doctorId;
    this.appointmentType = appointmentType;
    this.appointmentDate = appointmentDate;
    this.status = status;
    this.userUsername = userUsername;
    this.serviceName = serviceName;
    this.doctorName = doctorName;
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

  public Long getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(Long doctorId) {
    this.doctorId = doctorId;
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

  public String getDoctorName() {
    return doctorName;
  }

  public void setDoctorName(String doctorName) {
    this.doctorName = doctorName;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public String getDoctorEmail() {
    return doctorEmail;
  }

  public void setDoctorEmail(String doctorEmail) {
    this.doctorEmail = doctorEmail;
  }

  public String getDoctorPhone() {
    return doctorPhone;
  }

  public void setDoctorPhone(String doctorPhone) {
    this.doctorPhone = doctorPhone;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getAliasName() {
    return aliasName;
  }

  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }
}