package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.PatientHistoryDTO;
import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.TestResultDTO;
import com.hivmedical.medical.dto.PrescriptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PatientHistoryService {
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private TestResultService testResultService;
    @Autowired
    private PrescriptionService prescriptionService;

    public PatientHistoryDTO getPatientHistory(Long patientId,
            String startDate,
            String endDate,
            String appointmentStatus,
            String appointmentType,
            String testStatus,
            Long testCategoryId,
            String prescriptionStatus,
            Long protocolId) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Định dạng ngày không hợp lệ. Định dạng đúng: yyyy-MM-dd");
        }

        final LocalDateTime finalStart = start;
        final LocalDateTime finalEnd = end;

        // Filter Appointments
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId).stream()
                .filter(a -> (finalStart == null || (a.getAppointmentDate() != null
                        && LocalDateTime.parse(a.getAppointmentDate()).isAfter(finalStart.minusSeconds(1))))
                        && (finalEnd == null || (a.getAppointmentDate() != null
                                && LocalDateTime.parse(a.getAppointmentDate()).isBefore(finalEnd.plusSeconds(1))))
                        && (appointmentStatus == null || appointmentStatus.isEmpty()
                                || (a.getStatus() != null && a.getStatus().equalsIgnoreCase(appointmentStatus)))
                        && (appointmentType == null || appointmentType.isEmpty()
                                || (a.getAppointmentType() != null
                                        && a.getAppointmentType().equalsIgnoreCase(appointmentType))))
                .collect(Collectors.toList());

        // Filter Test Results
        List<TestResultDTO> testResults = testResultService.getResultsByPatientDTO(patientId).stream()
                .filter(t -> (finalStart == null
                        || (t.getCreatedAt() != null && t.getCreatedAt().isAfter(finalStart.minusSeconds(1))))
                        && (finalEnd == null
                                || (t.getCreatedAt() != null && t.getCreatedAt().isBefore(finalEnd.plusSeconds(1))))
                        && (testStatus == null || testStatus.isEmpty()
                                || (t.getStatus() != null && t.getStatus().equalsIgnoreCase(testStatus)))
                        && (testCategoryId == null || testCategoryId == 0
                                || (t.getTestCategoryId() != null
                                        && Objects.equals(t.getTestCategoryId(), testCategoryId))))
                .collect(Collectors.toList());

        // Filter Prescriptions
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId).stream()
                .filter(p -> (finalStart == null
                        || (p.getPrescribedDate() != null && p.getPrescribedDate().isAfter(finalStart.minusSeconds(1))))
                        && (finalEnd == null || (p.getPrescribedDate() != null
                                && p.getPrescribedDate().isBefore(finalEnd.plusSeconds(1))))
                        && (prescriptionStatus == null || prescriptionStatus.isEmpty()
                                || (p.getStatus() != null && p.getStatus().equalsIgnoreCase(prescriptionStatus)))
                        && (protocolId == null || protocolId == 0
                                || (p.getProtocolId() != null && Objects.equals(p.getProtocolId(), protocolId))))
                .collect(Collectors.toList());

        return new PatientHistoryDTO(appointments, testResults, prescriptions);
    }

    // Hàm cũ để giữ tương thích
    public PatientHistoryDTO getPatientHistory(Long patientId) {
        return getPatientHistory(patientId, null, null, null, null, null, null, null, null);
    }
}