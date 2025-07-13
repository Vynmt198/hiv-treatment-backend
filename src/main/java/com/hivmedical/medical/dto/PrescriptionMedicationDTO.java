package com.hivmedical.medical.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrescriptionMedicationDTO {

    @JsonProperty("medicationId")
    private Long medicationId;

    @JsonProperty("dosage")
    private String dosage;

    @JsonProperty("frequency")
    private String frequency;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("quantity")
    private Integer quantity;

    // Constructors
    public PrescriptionMedicationDTO() {
    }

    public PrescriptionMedicationDTO(Long medicationId, String dosage, String frequency,
            String instructions, Integer quantity) {
        this.medicationId = medicationId;
        this.dosage = dosage;
        this.frequency = frequency;
        this.instructions = instructions;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}