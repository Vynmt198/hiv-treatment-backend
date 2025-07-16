package com.hivmedical.medical.dto;

public class PatientRegisterByStaffRequest {
    private String fullName;
    private String gender;
    private String dateOfBirth; // ISO format hoặc dd/MM/yyyy
    private String phoneNumber;
    private String address;
    private String email; // dùng để tạo account
    // Có thể thêm password nếu muốn gửi cho bệnh nhân, hoặc để mặc định

    // Getters và Setters
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}