package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "Doctor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Nationalized
  @Column(columnDefinition = "NVARCHAR(MAX)", nullable = false)
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
  @Column(columnDefinition = "NVARCHAR(255)")
  private String workingSchedule;

  @Column
  private String imageUrl;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
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

}