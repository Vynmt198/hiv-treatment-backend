package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor")
public class Doctor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng id riêng cho Doctor
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column
  private String fullName;

  @Column
  private String phoneNumber;

  @Column
  private String specialization;

  @Column
  private String qualification;

  @Column
  private String workingSchedule;

  @Column
  private String imageUrl;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false) // Thêm khóa ngoại liên kết với users
  private UserEntity user;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  // Constructors
  public Doctor() {}

  public Doctor(Long id, String email, String fullName, String phoneNumber, String specialization, String qualification,
                String workingSchedule, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt, UserEntity user) {
    this.id = id;
    this.email = email;
    this.fullName = fullName;
    this.phoneNumber = phoneNumber;
    this.specialization = specialization;
    this.qualification = qualification;
    this.workingSchedule = workingSchedule;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.user = user;
  }

  // Getters and Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public String getPhoneNumber() { return phoneNumber; }
  public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
  public String getSpecialization() { return specialization; }
  public void setSpecialization(String specialization) { this.specialization = specialization; }
  public String getQualification() { return qualification; }
  public void setQualification(String qualification) { this.qualification = qualification; }
  public String getWorkingSchedule() { return workingSchedule; }
  public void setWorkingSchedule(String workingSchedule) { this.workingSchedule = workingSchedule; }
  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public UserEntity getUser() { return user; }
  public void setUser(UserEntity user) { this.user = user; }
}