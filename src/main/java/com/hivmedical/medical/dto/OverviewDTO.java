package com.hivmedical.medical.dto;

public class OverviewDTO {
    private long totalUsers;
    private long newPatientsThisMonth;
    private long activeDoctors;
    private long activeStaff;
    private double userGrowthRate;

    public OverviewDTO() {}

    public OverviewDTO(long totalUsers, long newPatientsThisMonth, long activeDoctors, long activeStaff, double userGrowthRate) {
        this.totalUsers = totalUsers;
        this.newPatientsThisMonth = newPatientsThisMonth;
        this.activeDoctors = activeDoctors;
        this.activeStaff = activeStaff;
        this.userGrowthRate = userGrowthRate;
    }

    // Getters and setters...
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getNewPatientsThisMonth() { return newPatientsThisMonth; }
    public void setNewPatientsThisMonth(long newPatientsThisMonth) { this.newPatientsThisMonth = newPatientsThisMonth; }
    public long getActiveDoctors() { return activeDoctors; }
    public void setActiveDoctors(long activeDoctors) { this.activeDoctors = activeDoctors; }
    public long getActiveStaff() { return activeStaff; }
    public void setActiveStaff(long activeStaff) { this.activeStaff = activeStaff; }
    public double getUserGrowthRate() { return userGrowthRate; }
    public void setUserGrowthRate(double userGrowthRate) { this.userGrowthRate = userGrowthRate; }
}