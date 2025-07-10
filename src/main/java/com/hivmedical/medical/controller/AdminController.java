package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.DoctorDTO;
import com.hivmedical.medical.service.AppointmentService;
import java.util.List;
import java.util.Map;

import com.hivmedical.medical.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  @Autowired
  private AppointmentService appointmentService;
    @Autowired
    private UserService userService;

  @GetMapping("/appointments")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
    List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
    return ResponseEntity.ok(appointments);
  }

  @PostMapping("/appointments")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AppointmentDTO> createAppointmentForAdmin(@RequestBody AppointmentDTO dto) {
    AppointmentDTO createdAppointment = appointmentService.createAppointment(dto); // Reuse existing method
    return ResponseEntity.ok(createdAppointment);
  }
  @PostMapping("/doctors/official")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createOfficialDoctorAccount(@Valid @RequestBody DoctorDTO dto) {
    try {
      Map<String, Object> response = userService.createOfficialDoctorAccount(dto);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}


