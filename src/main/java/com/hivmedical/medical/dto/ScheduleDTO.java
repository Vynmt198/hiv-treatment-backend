package com.hivmedical.medical.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduleDTO {
  private Long id;
  private Long doctorId;
  private LocalDate date;
  private List<String> timeSlots;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private boolean isAvailable;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public ScheduleDTO() {
  }

  public ScheduleDTO(Long id, Long doctorId, LocalDate date, List<String> timeSlots,
      LocalDateTime startTime, LocalDateTime endTime, boolean isAvailable,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.doctorId = doctorId;
    this.date = date;
    this.timeSlots = timeSlots;
    this.startTime = startTime;
    this.endTime = endTime;
    this.isAvailable = isAvailable;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public List<String> getTimeSlots() {
    return timeSlots;
  }

  public void setTimeSlots(List<String> timeSlots) {
    this.timeSlots = timeSlots;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}