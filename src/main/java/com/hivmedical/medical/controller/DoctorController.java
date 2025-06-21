package com.hivmedical.medical.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
  @GetMapping("/patients")
  @PreAuthorize("hasRole('DOCTOR')")
  public ResponseEntity<String> getPatients() {
    return ResponseEntity.ok("List of patients for doctor");
  }
}