package com.hivmedical.medical.dto;

import java.util.List;

public class PatientHistoryDTO {
    private List<AppointmentDTO> appointments;
    private List<TestResultDTO> testResults;
    private List<PrescriptionDTO> prescriptions;

    public PatientHistoryDTO() {
    }

    public PatientHistoryDTO(List<AppointmentDTO> appointments, List<TestResultDTO> testResults,
            List<PrescriptionDTO> prescriptions) {
        this.appointments = appointments;
        this.testResults = testResults;
        this.prescriptions = prescriptions;
    }

    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    public List<TestResultDTO> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResultDTO> testResults) {
        this.testResults = testResults;
    }

    public List<PrescriptionDTO> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<PrescriptionDTO> prescriptions) {
        this.prescriptions = prescriptions;
    }
}