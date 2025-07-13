package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Account patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false)
    private ARVProtocol protocol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private AppointmentEntity appointment;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String customInstructions; // Hướng dẫn tùy chỉnh cho bệnh nhân

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String dosageAdjustments; // Điều chỉnh liều lượng nếu cần

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes; // Ghi chú của bác sĩ

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PrescriptionStatus status;

    @Column(nullable = false)
    private LocalDateTime prescribedDate;

    private LocalDateTime startDate; // Ngày bắt đầu điều trị

    private LocalDateTime endDate; // Ngày kết thúc điều trị (nếu có)

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = PrescriptionStatus.ACTIVE;
        }
        if (prescribedDate == null) {
            prescribedDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}