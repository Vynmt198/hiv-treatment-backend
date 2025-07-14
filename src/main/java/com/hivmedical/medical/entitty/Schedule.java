package com.hivmedical.medical.entitty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table
public class Schedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Doctor doctor;

  @Column(nullable = false)
  private LocalDate date;

  @Nationalized
  @Column(columnDefinition = "NVARCHAR(MAX)")
  private String timeSlots;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private LocalDateTime endTime;

  @Column(nullable = false)
  private boolean isAvailable;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status = Status.AVAILABLE;

  @Column
  private LocalDateTime pendingUntil;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (!isAvailable) {
      isAvailable = true;
    }
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public Schedule() {
  }

  public Schedule(Long id, Doctor doctor, LocalDate date, String timeSlots, LocalDateTime startTime,
      LocalDateTime endTime, boolean isAvailable, LocalDateTime createdAt,
      LocalDateTime updatedAt, Status status, LocalDateTime pendingUntil) {
    this.id = id;
    this.doctor = doctor;
    this.date = date;
    this.timeSlots = timeSlots;
    this.startTime = startTime;
    this.endTime = endTime;
    this.isAvailable = isAvailable;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.status = status;
    this.pendingUntil = pendingUntil;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getTimeSlots() {
    return timeSlots;
  }

  public void setTimeSlots(String timeSlots) {
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public LocalDateTime getPendingUntil() {
    return pendingUntil;
  }

  public void setPendingUntil(LocalDateTime pendingUntil) {
    this.pendingUntil = pendingUntil;
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

  public enum Status {
    AVAILABLE,
    PENDING,
    BOOKED
  }
}
