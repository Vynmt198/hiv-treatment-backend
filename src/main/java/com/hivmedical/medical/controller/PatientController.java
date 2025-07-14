package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.PatientProfileDTO;
import com.hivmedical.medical.dto.PatientHistoryDTO;
import com.hivmedical.medical.service.UserService;
import com.hivmedical.medical.service.PatientHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
  @Autowired
  private UserService userService;

  @Autowired
  private PatientHistoryService patientHistoryService;

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
      @RequestParam(required = false) Long protocolId) {
    PatientHistoryDTO history = patientHistoryService.getPatientHistory(
        patientId, startDate, endDate, appointmentStatus, appointmentType, testStatus, testCategoryId,
        prescriptionStatus, protocolId);
    return ResponseEntity.ok(history);
  }
}
