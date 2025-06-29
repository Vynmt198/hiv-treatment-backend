package com.hivmedical.medical.dto;

import java.time.LocalDate;
import java.util.List;

public class ScheduleDTO {
  private Long id;
  private Long doctorId;
  private LocalDate date;
  private List<String> timeSlots;


  public ScheduleDTO() {
  }

  public ScheduleDTO(Long id, Long doctorId, LocalDate date, List<String> timeSlots) {
    this.id = id;
    this.doctorId = doctorId;
    this.date = date;
    this.timeSlots = timeSlots;
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
}
