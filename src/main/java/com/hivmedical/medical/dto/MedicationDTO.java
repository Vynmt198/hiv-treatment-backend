package com.hivmedical.medical.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MedicationDTO {
    private Long id;
    private String name;
    private String genericName;
    private String brandName;
    private String description;
    @JsonProperty("drugClass")
    private String drugClass;
    private String mechanism;
    @JsonProperty(value = "dosageForms", access = JsonProperty.Access.WRITE_ONLY)
    private String dosageForms;

    @JsonProperty("dosageForm")
    private String dosageForm;

    @JsonProperty("strength")
    private String strength;

    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonProperty("storageConditions")
    private String storageConditions;

    private String standardDosage;
    private String sideEffects;
    private String contraindications;
    private String drugInteractions;
    @JsonProperty("isActive")
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public MedicationDTO() {
    }

    public MedicationDTO(Long id, String name, String genericName, String drugClass, boolean isActive) {
        this.id = id;
        this.name = name;
        this.genericName = genericName;
        this.drugClass = drugClass;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDrugClass() {
        return drugClass;
    }

    public void setDrugClass(String drugClass) {
        this.drugClass = drugClass;
    }

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getDosageForms() {
        return dosageForms;
    }

    public void setDosageForms(String dosageForms) {
        this.dosageForms = dosageForms;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
        // Map dosageForm to dosageForms for entity compatibility
        this.dosageForms = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
        // Map strength to standardDosage for entity compatibility
        this.standardDosage = strength;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getStorageConditions() {
        return storageConditions;
    }

    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }

    public String getStandardDosage() {
        return standardDosage;
    }

    public void setStandardDosage(String standardDosage) {
        this.standardDosage = standardDosage;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public String getContraindications() {
        return contraindications;
    }

    public void setContraindications(String contraindications) {
        this.contraindications = contraindications;
    }

    public String getDrugInteractions() {
        return drugInteractions;
    }

    public void setDrugInteractions(String drugInteractions) {
        this.drugInteractions = drugInteractions;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}