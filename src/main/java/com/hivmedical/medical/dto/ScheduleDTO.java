package com.hivmedical.medical.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduleDTO {
  private Long id;
  private Long doctorId;
  private String date;
  private List<String> timeSlots;
  private String startTime;
  private String endTime;
  private boolean isAvailable;
  private String createdAt;
  private String updatedAt;

  // Thông tin bác sĩ
  private String doctorName;
  private String doctorEmail;
  private String doctorPhone;
  private String doctorSpecialization;

  public ScheduleDTO() {
  }

  public ScheduleDTO(Long id, Long doctorId, String date, List<String> timeSlots,
      String startTime, String endTime, boolean isAvailable,
      String createdAt, String updatedAt) {
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

  public ScheduleDTO(Long id, Long doctorId, String date, List<String> timeSlots,
      String startTime, String endTime, boolean isAvailable,
      String createdAt, String updatedAt, String doctorName, String doctorEmail,
      String doctorPhone, String doctorSpecialization) {
    this.id = id;
    this.doctorId = doctorId;
    this.date = date;
    this.timeSlots = timeSlots;
    this.startTime = startTime;
    this.endTime = endTime;
    this.isAvailable = isAvailable;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.doctorName = doctorName;
    this.doctorEmail = doctorEmail;
    this.doctorPhone = doctorPhone;
    this.doctorSpecialization = doctorSpecialization;
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

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<String> getTimeSlots() {
    return timeSlots;
  }

  public void setTimeSlots(List<String> timeSlots) {
    this.timeSlots = timeSlots;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getDoctorName() {
    return doctorName;
  }

  public void setDoctorName(String doctorName) {
    this.doctorName = doctorName;
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

  public String getDoctorSpecialization() {
    return doctorSpecialization;
  }

  public void setDoctorSpecialization(String doctorSpecialization) {
    this.doctorSpecialization = doctorSpecialization;
  }
}