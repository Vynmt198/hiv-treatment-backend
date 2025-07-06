package com.hivmedical.medical.dto;

import java.time.LocalDate;

public class PatientProfileDTO {
    private String fullName;
    private String gender;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private String hivStatus;
    private LocalDate treatmentStartDate;

    // Getter/Setter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getHivStatus() {
        return hivStatus;
    }

    public void setHivStatus(String hivStatus) {
        this.hivStatus = hivStatus;
    }

    public LocalDate getTreatmentStartDate() {
        return treatmentStartDate;
    }

    public void setTreatmentStartDate(LocalDate treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
    }

    // Constructors
    public PatientProfileDTO() {
    }

    public PatientProfileDTO(String fullName, String gender, String phone, String address, LocalDate birthDate,
            String hivStatus, LocalDate treatmentStartDate) {
        this.fullName = fullName;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.hivStatus = hivStatus;
        this.treatmentStartDate = treatmentStartDate;
    }
}
