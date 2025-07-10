package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_treatment_plans")
public class PatientTreatmentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private UserEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_plan_id", nullable = false)
    private TreatmentPlanTemplate treatmentPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    @Column
    private LocalDateTime treatmentDate;

    @Column
    private String medicalRecord;

    @Column
    private String healthcarePlan;

    // Constructors
    public PatientTreatmentPlan() {
        this.assignedAt = LocalDateTime.now();
    }

    public PatientTreatmentPlan(Long id, UserEntity patient, TreatmentPlanTemplate treatmentPlan, Doctor doctor,
                                LocalDateTime assignedAt, LocalDateTime treatmentDate, String medicalRecord, String healthcarePlan) {
        this.id = id;
        this.patient = patient;
        this.treatmentPlan = treatmentPlan;
        this.doctor = doctor;
        this.assignedAt = assignedAt;
        this.treatmentDate = treatmentDate;
        this.medicalRecord = medicalRecord;
        this.healthcarePlan = healthcarePlan;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserEntity getPatient() { return patient; }
    public void setPatient(UserEntity patient) { this.patient = patient; }
    public TreatmentPlanTemplate getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(TreatmentPlanTemplate treatmentPlan) { this.treatmentPlan = treatmentPlan; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    public LocalDateTime getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(LocalDateTime treatmentDate) { this.treatmentDate = treatmentDate; }
    public String getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(String medicalRecord) { this.medicalRecord = medicalRecord; }
    public String getHealthcarePlan() { return healthcarePlan; }
    public void setHealthcarePlan(String healthcarePlan) { this.healthcarePlan = healthcarePlan; }
}