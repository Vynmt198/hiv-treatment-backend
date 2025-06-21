package com.hivmedical.medical.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
  @GetMapping("/appointments")
  @PreAuthorize("hasRole('STAFF')")
  public ResponseEntity<String> getAppointments() {
    return ResponseEntity.ok("List of appointments for staff");
  }
}
