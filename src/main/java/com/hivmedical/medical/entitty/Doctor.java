package com.hivmedical.medical.entitty;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Doctor")
public class Doctor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fullName;

  private String specialization;
  private String qualification;
  private String email;
  private String phoneNumber;
  private String workingSchedule;

  @Column
  private String imageUrl;

  public Doctor() {}

  public Doctor(Long id, String fullName, String specialization, String qualification,
      String email, String phoneNumber, String workingSchedule, String imageUrl) {
    this.id = id;
    this.fullName = fullName;
    this.specialization = specialization;
    this.qualification = qualification;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.workingSchedule = workingSchedule;
    this.imageUrl = imageUrl;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }

  public String getSpecialization() { return specialization; }
  public void setSpecialization(String specialization) { this.specialization = specialization; }

  public String getQualification() { return qualification; }
  public void setQualification(String qualification) { this.qualification = qualification; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPhoneNumber() { return phoneNumber; }
  public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

  public String getWorkingSchedule() { return workingSchedule; }
  public void setWorkingSchedule(String workingSchedule) { this.workingSchedule = workingSchedule; }

  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}