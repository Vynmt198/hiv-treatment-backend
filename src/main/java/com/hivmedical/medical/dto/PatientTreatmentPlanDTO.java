package com.hivmedical.medical.dto;

public class PatientTreatmentPlanDTO {
    private Long id;
    private Long patientId;
    private Long treatmentPlanId;
    private String assignedAt;
    private String treatmentDate;
    private String medicalRecord;
    private String healthcarePlan;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getTreatmentPlanId() { return treatmentPlanId; }
    public void setTreatmentPlanId(Long treatmentPlanId) { this.treatmentPlanId = treatmentPlanId; }
    public String getAssignedAt() { return assignedAt; }
    public void setAssignedAt(String assignedAt) { this.assignedAt = assignedAt; }
    public String getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(String treatmentDate) { this.treatmentDate = treatmentDate; }
    public String getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(String medicalRecord) { this.medicalRecord = medicalRecord; }
    public String getHealthcarePlan() { return healthcarePlan; }
    public void setHealthcarePlan(String healthcarePlan) { this.healthcarePlan = healthcarePlan; }
}