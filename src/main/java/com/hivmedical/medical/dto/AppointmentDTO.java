package com.hivmedical.medical.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDTO {
  private Long id;
  private Long userId;

  @NotNull(message = "Doctor ID is required")
  private Long doctorId;

  @NotNull(message = "Date is required")
  private LocalDate date;

  @NotNull(message = "Time is required")
  private LocalTime time;

  @NotNull(message = "Type is required")
  @Pattern(regexp = "EXPLORATION|CONSULTATION", message = "Type must be EXPLORATION or CONSULTATION")
  private String type;

  private boolean isAnonymous;
  private String status;
  private String referenceCode;

  public AppointmentDTO() {
  }

  public AppointmentDTO(Long id, Long userId, Long doctorId, LocalDate date, LocalTime time,
      String type, boolean isAnonymous, String status, String referenceCode) {
    this.id = id;
    this.userId = userId;
    this.doctorId = doctorId;
    this.date = date;
    this.time = time;
    this.type = type;
    this.isAnonymous = isAnonymous;
    this.status = status;
    this.referenceCode = referenceCode;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(Long doctorId) {
    this.doctorId = doctorId;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isAnonymous() {
    return isAnonymous;
  }

  public void setAnonymous(boolean anonymous) {
    isAnonymous = anonymous;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getReferenceCode() {
    return referenceCode;
  }

  public void setReferenceCode(String referenceCode) {
    this.referenceCode = referenceCode;
  }
}
