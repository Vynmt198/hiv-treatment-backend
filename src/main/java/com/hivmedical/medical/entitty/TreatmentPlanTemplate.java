package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_plan_templates")
public class TreatmentPlanTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String drugComponents;

    @Column
    private String dosage;

    @Column
    private String indications;

    @Column
    private String sideEffects;

    @Column
    private String monitoringEffectiveness;

    @Column
    private String effectivenessEvaluation;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Doctor createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public TreatmentPlanTemplate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TreatmentPlanTemplate(Long id, String name, String drugComponents, String dosage, String indications,
                                 String sideEffects, String monitoringEffectiveness, String effectivenessEvaluation,
                                 LocalDate startDate, LocalDate endDate, String status, UserEntity user, Doctor createdBy,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.drugComponents = drugComponents;
        this.dosage = dosage;
        this.indications = indications;
        this.sideEffects = sideEffects;
        this.monitoringEffectiveness = monitoringEffectiveness;
        this.effectivenessEvaluation = effectivenessEvaluation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.user = user;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDrugComponents() { return drugComponents; }
    public void setDrugComponents(String drugComponents) { this.drugComponents = drugComponents; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getIndications() { return indications; }
    public void setIndications(String indications) { this.indications = indications; }
    public String getSideEffects() { return sideEffects; }
    public void setSideEffects(String sideEffects) { this.sideEffects = sideEffects; }
    public String getMonitoringEffectiveness() { return monitoringEffectiveness; }
    public void setMonitoringEffectiveness(String monitoringEffectiveness) { this.monitoringEffectiveness = monitoringEffectiveness; }
    public String getEffectivenessEvaluation() { return effectivenessEvaluation; }
    public void setEffectivenessEvaluation(String effectivenessEvaluation) { this.effectivenessEvaluation = effectivenessEvaluation; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public Doctor getCreatedBy() { return createdBy; }
    public void setCreatedBy(Doctor createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}