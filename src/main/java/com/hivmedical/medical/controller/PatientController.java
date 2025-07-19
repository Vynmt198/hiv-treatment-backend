package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.PatientProfileDTO;
import com.hivmedical.medical.dto.PatientHistoryDTO;
import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.TestResultDTO;
import com.hivmedical.medical.dto.PrescriptionDTO;
import com.hivmedical.medical.service.UserService;
import com.hivmedical.medical.service.PatientHistoryService;
import com.hivmedical.medical.service.AppointmentService;
import com.hivmedical.medical.service.TestResultService;
import com.hivmedical.medical.service.PrescriptionService;
import com.hivmedical.medical.entitty.Account;
import com.hivmedical.medical.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
  @Autowired
  private UserService userService;

  @Autowired
  private PatientHistoryService patientHistoryService;

  @Autowired
  private AppointmentService appointmentService;

  @Autowired
  private TestResultService testResultService;

  @Autowired
  private PrescriptionService prescriptionService;

  @Autowired
  private AccountRepository accountRepository;

  @GetMapping("/profile")
  @PreAuthorize("hasRole('PATIENT')")
  public ResponseEntity<PatientProfileDTO> getProfilEntity(Authentication authentication) {
    String email = authentication.getName();
    PatientProfileDTO profile = userService.getPatientProfile(email);
    return ResponseEntity.ok(profile);
  }

  @PutMapping("/profile")
  @PreAuthorize("hasRole('PATIENT')")
  public ResponseEntity<PatientProfileDTO> updateProfile(
      @RequestBody @Valid PatientProfileDTO dto,
      Authentication authentication) {
    String email = authentication.getName();
    PatientProfileDTO updated = userService.updatePatientProfile(email, dto);
    return ResponseEntity.ok(updated);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
  public ResponseEntity<List<PatientProfileDTO>> getAllPatients() {
    return ResponseEntity.ok(userService.getAllPatients());
  }

  @GetMapping("/{patientId}/history")
  @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'STAFF', 'ADMIN')")
  public ResponseEntity<PatientHistoryDTO> getPatientHistory(
      @PathVariable Long patientId,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false) String appointmentStatus,
      @RequestParam(required = false) String appointmentType,
      @RequestParam(required = false) String testStatus,
      @RequestParam(required = false) Long testCategoryId,
      @RequestParam(required = false) String prescriptionStatus,
      @RequestParam(required = false) Long protocolId,
      Authentication authentication) {

    System.out.println("=== DEBUG: getPatientHistory called with patientId: " + patientId);
    System.out.println("=== DEBUG: Authentication: " + (authentication != null ? authentication.getName() : "null"));
    System.out
        .println("=== DEBUG: Authorities: " + (authentication != null ? authentication.getAuthorities() : "null"));

    try {
      PatientHistoryDTO history = patientHistoryService.getPatientHistory(
          patientId, startDate, endDate, appointmentStatus, appointmentType, testStatus, testCategoryId,
          prescriptionStatus, protocolId);

      System.out.println("=== DEBUG: History retrieved successfully");
      System.out.println("=== DEBUG: Appointments count: "
          + (history.getAppointments() != null ? history.getAppointments().size() : 0));
      System.out.println(
          "=== DEBUG: Test results count: " + (history.getTestResults() != null ? history.getTestResults().size() : 0));
      System.out.println("=== DEBUG: Prescriptions count: "
          + (history.getPrescriptions() != null ? history.getPrescriptions().size() : 0));

      return ResponseEntity.ok(history);
    } catch (Exception e) {
      System.err.println("=== ERROR: Failed to get patient history: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  // Endpoint test để kiểm tra patient history
  @GetMapping("/test/history/{patientId}")
  public ResponseEntity<Map<String, Object>> testPatientHistory(@PathVariable Long patientId) {
    Map<String, Object> result = new HashMap<>();

    try {
      System.out.println("=== DEBUG: Test endpoint called with patientId: " + patientId);

      // Test từng service riêng biệt
      try {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        result.put("appointmentsCount", appointments.size());
        result.put("appointments", appointments);
        System.out.println("=== DEBUG: Found " + appointments.size() + " appointments");

        // Debug chi tiết từng appointment
        for (AppointmentDTO appointment : appointments) {
          System.out.println("=== DEBUG: Appointment ID: " + appointment.getId());
          System.out.println("=== DEBUG: Appointment Date: " + appointment.getAppointmentDate());
          System.out.println("=== DEBUG: Appointment Status: " + appointment.getStatus());
          System.out.println("=== DEBUG: Appointment Type: " + appointment.getAppointmentType());
          System.out.println("=== DEBUG: Booking Mode: " + appointment.getBookingMode());
        }
      } catch (Exception e) {
        result.put("appointmentsError", e.getMessage());
        System.err.println("=== ERROR: Appointments error: " + e.getMessage());
      }

      try {
        List<TestResultDTO> testResults = testResultService.getResultsByPatientDTO(patientId);
        result.put("testResultsCount", testResults.size());
        result.put("testResults", testResults);
        System.out.println("=== DEBUG: Found " + testResults.size() + " test results");
      } catch (Exception e) {
        result.put("testResultsError", e.getMessage());
        System.err.println("=== ERROR: Test results error: " + e.getMessage());
      }

      try {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
        result.put("prescriptionsCount", prescriptions.size());
        result.put("prescriptions", prescriptions);
        System.out.println("=== DEBUG: Found " + prescriptions.size() + " prescriptions");
      } catch (Exception e) {
        result.put("prescriptionsError", e.getMessage());
        System.err.println("=== ERROR: Prescriptions error: " + e.getMessage());
      }

      result.put("status", "success");
    } catch (Exception e) {
      result.put("status", "error");
      result.put("error", e.getMessage());
      e.printStackTrace();
    }

    return ResponseEntity.ok(result);
  }

  // Endpoint test đơn giản để kiểm tra appointments không có filter
  @GetMapping("/test/appointments/{patientId}")
  public ResponseEntity<Map<String, Object>> testAppointmentsOnly(@PathVariable Long patientId) {
    Map<String, Object> result = new HashMap<>();

    try {
      System.out.println("=== DEBUG: Test appointments only endpoint called with patientId: " + patientId);

      List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
      result.put("appointmentsCount", appointments.size());
      result.put("appointments", appointments);

      System.out.println("=== DEBUG: Found " + appointments.size() + " appointments without any filter");

      // Debug chi tiết từng appointment
      for (AppointmentDTO appointment : appointments) {
        System.out.println("=== DEBUG: Appointment ID: " + appointment.getId());
        System.out.println("=== DEBUG: Appointment Date: " + appointment.getAppointmentDate());
        System.out.println("=== DEBUG: Appointment Status: " + appointment.getStatus());
        System.out.println("=== DEBUG: Appointment Type: " + appointment.getAppointmentType());
        System.out.println("=== DEBUG: Booking Mode: " + appointment.getBookingMode());
        System.out.println("=== DEBUG: Full Name: " + appointment.getFullName());
        System.out.println("=== DEBUG: Phone: " + appointment.getPhone());
        System.out.println("=== DEBUG: Gender: " + appointment.getGender());
      }

      result.put("status", "success");
    } catch (Exception e) {
      result.put("status", "error");
      result.put("error", e.getMessage());
      e.printStackTrace();
    }

    return ResponseEntity.ok(result);
  }

  // Endpoint test để kiểm tra patient history không có filter
  @GetMapping("/test/history-simple/{patientId}")
  public ResponseEntity<Map<String, Object>> testPatientHistorySimple(@PathVariable Long patientId) {
    Map<String, Object> result = new HashMap<>();

    try {
      System.out.println("=== DEBUG: Test simple history endpoint called with patientId: " + patientId);

      // Gọi PatientHistoryService với tất cả tham số null (không filter)
      PatientHistoryDTO history = patientHistoryService.getPatientHistory(patientId, null, null, null, null, null, null,
          null, null);

      result.put("appointmentsCount", history.getAppointments() != null ? history.getAppointments().size() : 0);
      result.put("appointments", history.getAppointments());
      result.put("testResultsCount", history.getTestResults() != null ? history.getTestResults().size() : 0);
      result.put("testResults", history.getTestResults());
      result.put("prescriptionsCount", history.getPrescriptions() != null ? history.getPrescriptions().size() : 0);
      result.put("prescriptions", history.getPrescriptions());

      System.out.println("=== DEBUG: Simple history - Appointments: "
          + (history.getAppointments() != null ? history.getAppointments().size() : 0));
      System.out.println("=== DEBUG: Simple history - Test Results: "
          + (history.getTestResults() != null ? history.getTestResults().size() : 0));
      System.out.println("=== DEBUG: Simple history - Prescriptions: "
          + (history.getPrescriptions() != null ? history.getPrescriptions().size() : 0));

      result.put("status", "success");
    } catch (Exception e) {
      result.put("status", "error");
      result.put("error", e.getMessage());
      e.printStackTrace();
    }

    return ResponseEntity.ok(result);
  }

  // Endpoint test để lấy tất cả appointments mà không có filter
  @GetMapping("/test/appointments-all/{patientId}")
  public ResponseEntity<Map<String, Object>> testAllAppointments(@PathVariable Long patientId) {
    Map<String, Object> result = new HashMap<>();

    try {
      System.out.println("=== DEBUG: Test all appointments endpoint called with patientId: " + patientId);

      List<AppointmentDTO> appointments = patientHistoryService.getAllAppointmentsForPatient(patientId);
      result.put("appointmentsCount", appointments.size());
      result.put("appointments", appointments);

      System.out.println("=== DEBUG: Found " + appointments.size() + " appointments using PatientHistoryService");

      result.put("status", "success");
    } catch (Exception e) {
      result.put("status", "error");
      result.put("error", e.getMessage());
      e.printStackTrace();
    }

    return ResponseEntity.ok(result);
  }

  // Endpoint test để kiểm tra mapping ID
  @GetMapping("/test/mapping/{patientId}")
  public ResponseEntity<Map<String, Object>> testIdMapping(@PathVariable Long patientId) {
    Map<String, Object> result = new HashMap<>();

    try {
      System.out.println("=== DEBUG: Test ID mapping endpoint called with patientId: " + patientId);

      // Test mapping ID
      Long accountId = appointmentService.getAccountIdFromPatientId(patientId);
      result.put("originalPatientId", patientId);
      result.put("mappedAccountId", accountId);

      if (accountId != null) {
        // Test lấy appointments với account ID đã map
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(accountId);
        result.put("appointmentsCount", appointments.size());
        result.put("appointments", appointments);
        System.out.println("=== DEBUG: Found " + appointments.size() + " appointments with mapped account ID");
      } else {
        result.put("appointmentsCount", 0);
        result.put("appointments", new ArrayList<>());
        System.out.println("=== DEBUG: No account ID found for patient ID " + patientId);
      }

      result.put("status", "success");
    } catch (Exception e) {
      result.put("status", "error");
      result.put("error", e.getMessage());
      e.printStackTrace();
    }

    return ResponseEntity.ok(result);
  }

  // Endpoint test để kiểm tra mapping ID với patientId trực tiếp
  @GetMapping("/test/mapping-direct/{patientId}")
  public ResponseEntity<Map<String, Object>> testIdMappingDirect(@PathVariable Long patientId) {
    Map<String, Object> result = new HashMap<>();

    try {
      System.out.println("=== DEBUG: Test direct ID mapping endpoint called with patientId: " + patientId);

      // Test mapping ID
      Long accountId = appointmentService.getAccountIdFromPatientId(patientId);
      result.put("originalPatientId", patientId);
      result.put("mappedAccountId", accountId);

      if (accountId != null) {
        // Test lấy appointments với patientId trực tiếp (sẽ sử dụng mapping bên trong)
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        result.put("appointmentsCount", appointments.size());
        result.put("appointments", appointments);
        System.out.println("=== DEBUG: Found " + appointments.size() + " appointments with direct patientId");
      } else {
        result.put("appointmentsCount", 0);
        result.put("appointments", new ArrayList<>());
        System.out.println("=== DEBUG: No account ID found for patient ID " + patientId);
      }

      result.put("status", "success");
    } catch (Exception e) {
      result.put("status", "error");
      result.put("error", e.getMessage());
      e.printStackTrace();
    }

    return ResponseEntity.ok(result);
  }

  // Endpoint test để kiểm tra tất cả accounts
  @GetMapping("/test/accounts")
  public ResponseEntity<Map<String, Object>> testAllAccounts() {
    Map<String, Object> result = new HashMap<>();

    try {
      System.out.println("=== DEBUG: Test all accounts endpoint called");

      List<Account> accounts = accountRepository.findAll();
      List<Map<String, Object>> accountList = new ArrayList<>();

      for (Account account : accounts) {
        Map<String, Object> accountInfo = new HashMap<>();
        accountInfo.put("id", account.getId());
        accountInfo.put("username", account.getUsername());
        accountInfo.put("email", account.getEmail());
        accountInfo.put("role", account.getRole());
        accountList.add(accountInfo);
        System.out.println("=== DEBUG: Account ID: " + account.getId() + ", Username: " + account.getUsername()
            + ", Email: " + account.getEmail());
      }

      result.put("accountsCount", accounts.size());
      result.put("accounts", accountList);
      result.put("status", "success");
    } catch (Exception e) {
      result.put("status", "error");
      result.put("error", e.getMessage());
      e.printStackTrace();
    }

    return ResponseEntity.ok(result);
  }
}
