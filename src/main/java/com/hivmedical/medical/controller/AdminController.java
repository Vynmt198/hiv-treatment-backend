package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.OverviewDTO;
import com.hivmedical.medical.dto.RegisterDoctorRequest;
import com.hivmedical.medical.service.AdminService;
import com.hivmedical.medical.service.AppointmentService;
import com.hivmedical.medical.service.UserService;

import java.util.List;
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

  @Autowired
  private AdminService adminService;

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
  @PostMapping("/register-doctor")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerDoctor(@RequestBody RegisterDoctorRequest request) {
      userService.registerDoctor(request);
      return ResponseEntity.ok("Tạo tài khoản và hồ sơ bác sĩ thành công!");
  }
  @GetMapping("/overview")
  @PreAuthorize("hasRole('ADMIN')")
  public OverviewDTO getOverview() {
    return adminService.getOverview();
}
@GetMapping("/total-patients")

public long getTotalPatients() {
    return adminService.getTotalPatients();
}

@GetMapping("/total-appointments")

public long getTotalAppointments() {
    return adminService.getTotalAppointments();
}

@GetMapping("/pending-appointments")

public long getPendingAppointments() {
    return adminService.getPendingAppointments();
}

@GetMapping("/total-doctors")
@PreAuthorize("hasRole('ADMIN')")
public long getTotalDoctors() {
    return adminService.getTotalDoctors();
}
 
}


