package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_results")
@Data
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Account patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "test_category_id")
    private TestCategory testCategory;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private AppointmentEntity appointment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TestStatus status;

    @Column(length = 255)
    private String resultValue;

    @Column(length = 1000)
    private String resultNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resultDate;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null)
            status = TestStatus.REQUESTED;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}