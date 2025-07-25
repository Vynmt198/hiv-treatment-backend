package com.hivmedical.medical.entitty;

import com.hivmedical.medical.entitty.AppointmentStatus;
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
  @JoinColumn(nullable = false)
  private Account user;

  @ManyToOne
  @JoinColumn(nullable = false)
  private ServiceEntity service;

  @ManyToOne
  private Doctor doctor;

  @ManyToOne
  @JoinColumn(name = "schedule_id", nullable = false)
  private Schedule schedule;

  @Column(nullable = false, columnDefinition = "NVARCHAR(50) COLLATE Vietnamese_CI_AS")
  private String appointmentType;

  private LocalDateTime appointmentDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "NVARCHAR(50) COLLATE Vietnamese_CI_AS")
  private AppointmentStatus status;

  @Column(length = 50)
  private String phone;

  @Column(length = 10)
  private String gender;

  @Column(length = 255, columnDefinition = "NVARCHAR(255)")
  private String fullName;

  @Column(columnDefinition = "NVARCHAR(MAX)")
  private String description;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (status == null) {
      status = AppointmentStatus.PENDING;
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

  public AppointmentEntity(Long id, Account user, ServiceEntity service, Doctor doctor,
      String appointmentType, LocalDateTime appointmentDate, AppointmentStatus status, LocalDateTime createdAt,
      LocalDateTime updatedAt, String phone, String gender, String description, Schedule schedule) {

    this.id = id;
    this.user = user;
    this.service = service;
    this.doctor = doctor;
    this.appointmentType = appointmentType;
    this.appointmentDate = appointmentDate;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.phone = phone;
    this.gender = gender;
    this.description = description;

    this.schedule = schedule;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Account getUser() {
    return user;
  }

  public void setUser(Account user) {
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

  public AppointmentStatus getStatus() {
    return status;
  }

  public void setStatus(AppointmentStatus status) {
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

  public Doctor getDoctor() {
    return doctor;
  }

  public void setDoctor(Doctor doctor) {
    this.doctor = doctor;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public void setSchedule(Schedule schedule) {
    this.schedule = schedule;
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

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public enum BookingMode {
    NORMAL,
    ONLINE,
    ANONYMOUS_ONLINE
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "booking_mode", nullable = false, columnDefinition = "NVARCHAR(50) COLLATE Vietnamese_CI_AS")
  private BookingMode bookingMode = BookingMode.NORMAL;

  public BookingMode getBookingMode() {
    return bookingMode;
  }

  public void setBookingMode(BookingMode bookingMode) {
    this.bookingMode = bookingMode;
  }

  @Column(name = "google_meet_link", length = 512)
  private String googleMeetLink;

  public String getGoogleMeetLink() {
    return googleMeetLink;
  }

  public void setGoogleMeetLink(String googleMeetLink) {
    this.googleMeetLink = googleMeetLink;
  }
}