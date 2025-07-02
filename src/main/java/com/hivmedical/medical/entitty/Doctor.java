package com.hivmedical.medical.entitty;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Doctor")
public class Doctor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Nationalized
  @Column(columnDefinition = "NVARCHAR(MAX)",nullable = false)
  private String fullName;

  @Nationalized
  private String specialization;

  @Nationalized
  private String qualification;

  @Column
  private String email;

  @Column
  private String phoneNumber;

  @Nationalized
  private String workingSchedule;

  @Column
  private String imageUrl;

  @Column( nullable = false)
  private LocalDateTime createdAt;

  @Column( nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public Doctor() {}

  public Doctor(Long id, String fullName, String specialization, String qualification, String email,
      String phoneNumber, String workingSchedule, String imageUrl, LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.fullName = fullName;
    this.specialization = specialization;
    this.qualification = qualification;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.workingSchedule = workingSchedule;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public String getQualification() {
    return qualification;
  }

  public void setQualification(String qualification) {
    this.qualification = qualification;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getWorkingSchedule() {
    return workingSchedule;
  }

  public void setWorkingSchedule(String workingSchedule) {
    this.workingSchedule = workingSchedule;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
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