package com.hivmedical.medical.entitty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "users")
@Builder
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @Column(name = "userid")
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

  @Column( nullable = false)
  private LocalDateTime registrationDate;

//  @Column(name = "lastLoginDate")
  private LocalDateTime lastLoginDate;

  @Column(length = 255)
  private String profilePictureUrl;

  @Column(nullable = false, length = 255)
  private String role = "member";

  @Column(nullable = false)
  private boolean enabled = false;


  @PrePersist
  protected void onCreate() {
    if (registrationDate == null) {
      registrationDate = LocalDateTime.now();
    }
    if (role == null || role.trim().isEmpty()) {
      role = "member";
    }
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

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public UserEntity() {
  }

  public UserEntity(Long userId, String username, String passwordHash, String fullName,
      String email,
      LocalDateTime registrationDate, LocalDateTime lastLoginDate, String profilePictureUrl,
      String role, boolean enabled) {
    this.userId = userId;
    this.username = username;
    this.passwordHash = passwordHash;
    this.fullName = fullName;
    this.email = email;
    this.registrationDate = registrationDate;
    this.lastLoginDate = lastLoginDate;
    this.profilePictureUrl = profilePictureUrl;
    this.role = role;
    this.enabled = enabled;
  }
}