package com.hivmedical.medical.dto;

public class TreatmentPlanTemplateDTO {
    private Long id;
    private String name;
    private String drugComponents;
    private String dosage;
    private String indications;
    private String sideEffects;
    private String monitoringEffectiveness;
    private String effectivenessEvaluation;
    private String startDate;
    private String endDate;
    private String status;
    private Long createdBy;

    public TreatmentPlanTemplateDTO() {}

    public TreatmentPlanTemplateDTO(Long id, String name, String drugComponents, String dosage, String indications,
                                    String sideEffects, String monitoringEffectiveness, String effectivenessEvaluation,
                                    String startDate, String endDate, String status, Long createdBy) {
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
        this.createdBy = createdBy;
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
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}