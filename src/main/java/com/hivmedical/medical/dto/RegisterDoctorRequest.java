package com.hivmedical.medical.dto;




public class RegisterDoctorRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String qualification;
    private String specialization;
    private String phoneNumber;
    private String imageUrl;
    private String workingSchedule;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getWorkingSchedule() { return workingSchedule; }
    public void setWorkingSchedule(String workingSchedule) { this.workingSchedule = workingSchedule; }
}
