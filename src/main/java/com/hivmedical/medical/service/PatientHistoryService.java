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

                System.out.println("=== DEBUG: PatientHistoryService.getPatientHistory called with patientId: "
                                + patientId);
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
                System.out.println("=== DEBUG: Getting appointments for patient: " + patientId);
                List<AppointmentDTO> allAppointments = appointmentService.getAppointmentsByPatient(patientId);
                System.out.println("=== DEBUG: Found " + allAppointments.size() + " appointments for patient "
                                + patientId);

                List<AppointmentDTO> appointments = allAppointments.stream()
                                .filter(a -> {
                                        System.out.println("=== DEBUG: Filtering appointment ID: " + a.getId());
                                        System.out.println("=== DEBUG: Appointment date: " + a.getAppointmentDate());
                                        System.out.println("=== DEBUG: Appointment status: " + a.getStatus());
                                        System.out.println("=== DEBUG: Appointment type: " + a.getAppointmentType());

                                        boolean dateFilter = true;
                                        if (finalStart != null && a.getAppointmentDate() != null) {
                                                try {
                                                        LocalDateTime appointmentDateTime = LocalDateTime
                                                                        .parse(a.getAppointmentDate());
                                                        dateFilter = appointmentDateTime
                                                                        .isAfter(finalStart.minusSeconds(1));
                                                        System.out.println(
                                                                        "=== DEBUG: Start date filter: " + dateFilter);
                                                } catch (Exception e) {
                                                        System.out.println("=== DEBUG: Error parsing appointment date: "
                                                                        + e.getMessage());
                                                        dateFilter = false;
                                                }
                                        }

                                        boolean endDateFilter = true;
                                        if (finalEnd != null && a.getAppointmentDate() != null) {
                                                try {
                                                        LocalDateTime appointmentDateTime = LocalDateTime
                                                                        .parse(a.getAppointmentDate());
                                                        endDateFilter = appointmentDateTime
                                                                        .isBefore(finalEnd.plusSeconds(1));
                                                        System.out.println(
                                                                        "=== DEBUG: End date filter: " + endDateFilter);
                                                } catch (Exception e) {
                                                        System.out.println(
                                                                        "=== DEBUG: Error parsing appointment date for end filter: "
                                                                                        + e.getMessage());
                                                        endDateFilter = false;
                                                }
                                        }

                                        boolean statusFilter = (appointmentStatus == null || appointmentStatus.isEmpty()
                                                        || (a.getStatus() != null && a.getStatus()
                                                                        .equalsIgnoreCase(appointmentStatus)));
                                        System.out.println("=== DEBUG: Status filter: " + statusFilter);

                                        boolean typeFilter = (appointmentType == null || appointmentType.isEmpty()
                                                        || (a.getAppointmentType() != null && a.getAppointmentType()
                                                                        .equalsIgnoreCase(appointmentType)));
                                        System.out.println("=== DEBUG: Type filter: " + typeFilter);

                                        boolean result = dateFilter && endDateFilter && statusFilter && typeFilter;
                                        System.out.println("=== DEBUG: Final filter result: " + result);

                                        return result;
                                })
                                .collect(Collectors.toList());

                System.out.println("=== DEBUG: Filtered to " + appointments.size() + " appointments");

                // Filter Test Results
                System.out.println("=== DEBUG: Getting test results for patient: " + patientId);
                List<TestResultDTO> allTestResults = testResultService.getResultsByPatientDTO(patientId);
                System.out.println(
                                "=== DEBUG: Found " + allTestResults.size() + " test results for patient " + patientId);

                List<TestResultDTO> testResults = allTestResults.stream()
                                .filter(t -> (finalStart == null
                                                || (t.getCreatedAt() != null && t.getCreatedAt()
                                                                .isAfter(finalStart.minusSeconds(1))))
                                                && (finalEnd == null
                                                                || (t.getCreatedAt() != null && t.getCreatedAt()
                                                                                .isBefore(finalEnd.plusSeconds(1))))
                                                && (testStatus == null || testStatus.isEmpty()
                                                                || (t.getStatus() != null && t.getStatus()
                                                                                .equalsIgnoreCase(testStatus)))
                                                && (testCategoryId == null || testCategoryId == 0
                                                                || (t.getTestCategoryId() != null
                                                                                && Objects.equals(t.getTestCategoryId(),
                                                                                                testCategoryId))))
                                .collect(Collectors.toList());

                System.out.println("=== DEBUG: Filtered to " + testResults.size() + " test results");

                // Filter Prescriptions
                System.out.println("=== DEBUG: Getting prescriptions for patient: " + patientId);
                List<PrescriptionDTO> allPrescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
                System.out.println("=== DEBUG: Found " + allPrescriptions.size() + " prescriptions for patient "
                                + patientId);

                List<PrescriptionDTO> prescriptions = allPrescriptions.stream()
                                .filter(p -> (finalStart == null
                                                || (p.getPrescribedDate() != null && p.getPrescribedDate()
                                                                .isAfter(finalStart.minusSeconds(1))))
                                                && (finalEnd == null || (p.getPrescribedDate() != null
                                                                && p.getPrescribedDate()
                                                                                .isBefore(finalEnd.plusSeconds(1))))
                                                && (prescriptionStatus == null || prescriptionStatus.isEmpty()
                                                                || (p.getStatus() != null && p.getStatus()
                                                                                .equalsIgnoreCase(prescriptionStatus)))
                                                && (protocolId == null || protocolId == 0
                                                                || (p.getProtocolId() != null && Objects.equals(
                                                                                p.getProtocolId(), protocolId))))
                                .collect(Collectors.toList());

                System.out.println("=== DEBUG: Filtered to " + prescriptions.size() + " prescriptions");

                PatientHistoryDTO result = new PatientHistoryDTO(appointments, testResults, prescriptions);
                System.out.println("=== DEBUG: Created PatientHistoryDTO with:");
                System.out.println("  - Appointments: "
                                + (result.getAppointments() != null ? result.getAppointments().size() : 0));
                System.out.println("  - Test Results: "
                                + (result.getTestResults() != null ? result.getTestResults().size() : 0));
                System.out.println("  - Prescriptions: "
                                + (result.getPrescriptions() != null ? result.getPrescriptions().size() : 0));

                return result;
        }

        // Hàm cũ để giữ tương thích
        public PatientHistoryDTO getPatientHistory(Long patientId) {
                return getPatientHistory(patientId, null, null, null, null, null, null, null, null);
        }

        // Hàm test để lấy tất cả appointments mà không có filter
        public List<AppointmentDTO> getAllAppointmentsForPatient(Long patientId) {
                System.out.println("=== DEBUG: getAllAppointmentsForPatient called with patientId: " + patientId);
                List<AppointmentDTO> allAppointments = appointmentService.getAppointmentsByPatient(patientId);
                System.out.println("=== DEBUG: Found " + allAppointments.size() + " appointments without any filter");

                // Debug chi tiết từng appointment
                for (AppointmentDTO appointment : allAppointments) {
                        System.out.println("=== DEBUG: Appointment ID: " + appointment.getId());
                        System.out.println("=== DEBUG: Appointment Date: " + appointment.getAppointmentDate());
                        System.out.println("=== DEBUG: Appointment Status: " + appointment.getStatus());
                        System.out.println("=== DEBUG: Appointment Type: " + appointment.getAppointmentType());
                        System.out.println("=== DEBUG: Booking Mode: " + appointment.getBookingMode());
                }

                return allAppointments;
        }
}