package com.hivmedical.medical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DoctorDTO {
  private Long id;
  @NotBlank(message = "Full name is required")
  private String fullName;
  private String specialization;
  private String qualification;
  @NotBlank(message = "Email is required")
  private String email;
  private String phoneNumber;
  private String workingSchedule;
  private String imageUrl;

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
