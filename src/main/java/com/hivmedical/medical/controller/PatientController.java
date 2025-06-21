package com.hivmedical.medical.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
  @GetMapping("/profile")
  @PreAuthorize("hasRole('PATIENT')")
  public ResponseEntity<String> getPatientProfile() {
    return ResponseEntity.ok("Patient Profile");
  }
}
