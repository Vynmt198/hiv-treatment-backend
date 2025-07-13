package com.hivmedical.medical.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PrescriptionDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    @JsonProperty("protocolId")
    private Long protocolId;

    @JsonProperty("arvProtocolId")
    private Long arvProtocolId;
    private String protocolName;

    @JsonProperty("medications")
    private List<PrescriptionMedicationDTO> medications;
    private Long appointmentId;
    private String customInstructions;
    private String dosageAdjustments;
    private String notes;
    private String status;

    @JsonProperty("prescriptionDate")
    private String prescriptionDate;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    private LocalDateTime prescribedDate;
    private LocalDateTime startDateLocal;
    private LocalDateTime endDateLocal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public PrescriptionDTO() {
    }

    public PrescriptionDTO(Long id, Long patientId, String patientName, Long doctorId, String doctorName,
            Long protocolId, String protocolName, String status, LocalDateTime prescribedDate) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.protocolId = protocolId;
        this.protocolName = protocolName;
        this.status = status;
        this.prescribedDate = prescribedDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Long getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Long protocolId) {
        this.protocolId = protocolId;
    }

    public Long getArvProtocolId() {
        return arvProtocolId;
    }

    public void setArvProtocolId(Long arvProtocolId) {
        this.arvProtocolId = arvProtocolId;
        // Map arvProtocolId to protocolId for entity compatibility
        this.protocolId = arvProtocolId;
    }

    public List<PrescriptionMedicationDTO> getMedications() {
        return medications;
    }

    public void setMedications(List<PrescriptionMedicationDTO> medications) {
        this.medications = medications;
    }

    public String getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(String prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCustomInstructions() {
        return customInstructions;
    }

    public void setCustomInstructions(String customInstructions) {
        this.customInstructions = customInstructions;
    }

    public String getDosageAdjustments() {
        return dosageAdjustments;
    }

    public void setDosageAdjustments(String dosageAdjustments) {
        this.dosageAdjustments = dosageAdjustments;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPrescribedDate() {
        return prescribedDate;
    }

    public void setPrescribedDate(LocalDateTime prescribedDate) {
        this.prescribedDate = prescribedDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getStartDateLocal() {
        return startDateLocal;
    }

    public void setStartDateLocal(LocalDateTime startDateLocal) {
        this.startDateLocal = startDateLocal;
    }

    public LocalDateTime getEndDateLocal() {
        return endDateLocal;
    }

    public void setEndDateLocal(LocalDateTime endDateLocal) {
        this.endDateLocal = endDateLocal;
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