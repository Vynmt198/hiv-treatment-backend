package com.hivmedical.medical.entitty;

import java.io.Serializable;
import java.util.Objects;

public class PatientTreatmentPlanId implements Serializable {
    private Long patient;
    private Long treatmentPlan;

    public PatientTreatmentPlanId() {}

    public PatientTreatmentPlanId(Long patient, Long treatmentPlan) {
        this.patient = patient;
        this.treatmentPlan = treatmentPlan;
    }


    public Long getPatient() { return patient; }
    public void setPatient(Long patient) { this.patient = patient; }
    public Long getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(Long treatmentPlan) { this.treatmentPlan = treatmentPlan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientTreatmentPlanId that = (PatientTreatmentPlanId) o;
        return Objects.equals(patient, that.patient) && Objects.equals(treatmentPlan, that.treatmentPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patient, treatmentPlan);
    }
}