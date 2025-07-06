package com.hivmedical.medical.entitty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import com.hivmedical.medical.entitty.Role;

@Entity
@Table(name = "users")
@Builder
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // @Column(name = "userid")
  private Long userId;

  @Nationalized
  @Column(nullable = false, unique = true, length = 255)
  private String username;

  @Column(nullable = false, length = 255)
  private String passwordHash;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Nationalized
  @Column(length = 255)
  private String fullName;

  @Column(nullable = false)
  private LocalDateTime registrationDate;

  // @Column(name = "lastLoginDate")
  private LocalDateTime lastLoginDate;

  @Column(length = 255)
  private String profilePictureUrl;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(nullable = false)
  private boolean enabled = false;

  @Column(length = 50)
  private String phone;

  @Column(length = 10)
  private String gender;

  @Column(length = 255)
  private String address;

  @Column(nullable = false)
  private LocalDate birthDate;

  @Column(length = 255)
  private String hivStatus;

  @Column(nullable = false)
  private LocalDate treatmentStartDate;

  @PrePersist
  protected void onCreate() {
    if (registrationDate == null) {
      registrationDate = LocalDateTime.now();
    }
    if (role == null) {
      role = Role.PATIENT;
    }
    if (username == null && email != null) {
      username = email; // Đồng bộ username với email
    }
  }

  public UserEntity() {
  }

  public UserEntity(Long userId, String username, String passwordHash, String email,
      String fullName,
      LocalDateTime registrationDate, LocalDateTime lastLoginDate, String profilePictureUrl,
      Role role, boolean enabled, String phone, String gender, String address, LocalDate birthDate, String hivStatus,
      LocalDate treatmentStartDate) {
    this.userId = userId;
    this.username = username;
    this.passwordHash = passwordHash;
    this.email = email;
    this.fullName = fullName;
    this.registrationDate = registrationDate;
    this.lastLoginDate = lastLoginDate;
    this.profilePictureUrl = profilePictureUrl;
    this.role = role;
    this.enabled = enabled;
    this.phone = phone;
    this.gender = gender;
    this.address = address;
    this.birthDate = birthDate;
    this.hivStatus = hivStatus;
    this.treatmentStartDate = treatmentStartDate;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public LocalDateTime getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(LocalDateTime registrationDate) {
    this.registrationDate = registrationDate;
  }

  public LocalDateTime getLastLoginDate() {
    return lastLoginDate;
  }

  public void setLastLoginDate(LocalDateTime lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  public String getProfilePictureUrl() {
    return profilePictureUrl;
  }

  public void setProfilePictureUrl(String profilePictureUrl) {
    this.profilePictureUrl = profilePictureUrl;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public String getHivStatus() {
    return hivStatus;
  }

  public void setHivStatus(String hivStatus) {
    this.hivStatus = hivStatus;
  }

  public LocalDate getTreatmentStartDate() {
    return treatmentStartDate;
  }

  public void setTreatmentStartDate(LocalDate treatmentStartDate) {
    this.treatmentStartDate = treatmentStartDate;
  }
}