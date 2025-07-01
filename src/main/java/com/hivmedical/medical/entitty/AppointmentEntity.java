package com.hivmedical.medical.entitty;

import com.hivmedical.medical.entitty.ServiceEntity;
import com.hivmedical.medical.entitty.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
public class AppointmentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn( nullable = false)
  private UserEntity user;

  @ManyToOne
  @JoinColumn(nullable = false)
  private ServiceEntity service;

  @Column( nullable = false, columnDefinition = "NVARCHAR(50) COLLATE Vietnamese_CI_AS")
  private String appointmentType;


  private LocalDateTime appointmentDate;

  @Column( nullable = false, columnDefinition = "NVARCHAR(50) COLLATE Vietnamese_CI_AS")
  private String status;


  private LocalDateTime createdAt;


  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (status == null) {
      status = "PENDING";
    }
    if (appointmentType == null) {
      appointmentType = "FIRST_VISIT";
    }
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public AppointmentEntity() {
  }

  public AppointmentEntity(Long id, UserEntity user, ServiceEntity service, String appointmentType,
      LocalDateTime appointmentDate, String status, LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.user = user;
    this.service = service;
    this.appointmentType = appointmentType;
    this.appointmentDate = appointmentDate;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
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

  public ServiceEntity getService() {
    return service;
  }

  public void setService(ServiceEntity service) {
    this.service = service;
  }

  public String getAppointmentType() {
    return appointmentType;
  }

  public void setAppointmentType(String appointmentType) {
    this.appointmentType = appointmentType;
  }

  public LocalDateTime getAppointmentDate() {
    return appointmentDate;
  }

  public void setAppointmentDate(LocalDateTime appointmentDate) {
    this.appointmentDate = appointmentDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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