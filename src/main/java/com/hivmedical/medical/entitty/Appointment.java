package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = true)
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "doctor_id")
  private Doctor doctor;

  private LocalDate date;
  private LocalTime time;
  private String type;
  private boolean isAnonymous;
  private String status;

  @Column(name = "reference_code")
  private String referenceCode;

  public Appointment(Long id, UserEntity user, Doctor doctor, LocalDate date, LocalTime time,
      String type, boolean isAnonymous, String status, String referenceCode) {
    this.id = id;
    this.user = user;
    this.doctor = doctor;
    this.date = date;
    this.time = time;
    this.type = type;
    this.isAnonymous = isAnonymous;
    this.status = status;
    this.referenceCode = referenceCode;
  }

  public Appointment() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public Doctor getDoctor() {
    return doctor;
  }

  public void setDoctor(Doctor doctor) {
    this.doctor = doctor;
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