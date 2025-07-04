package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.OnlineAppointmentDTO;
import com.hivmedical.medical.dto.AnonymousOnlineDTO;
import com.hivmedical.medical.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

  @Autowired
  private AppointmentService appointmentService;

  @PostMapping
  @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
  public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
    return ResponseEntity.ok(appointmentService.createAppointment(appointmentDTO));
  }

  @GetMapping("/me")
  @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
  public ResponseEntity<List<AppointmentDTO>> getUserAppointments() {
    return ResponseEntity.ok(appointmentService.getUserAppointments());
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
    return ResponseEntity.ok(appointmentService.getAllAppointments());
  }

  @PostMapping("/online")
  public ResponseEntity<AppointmentDTO> createOnlineAppointment(@Valid @RequestBody OnlineAppointmentDTO dto,
      BindingResult result) {
    if (result.hasErrors()) {
      throw new IllegalArgumentException(result.getAllErrors().get(0).getDefaultMessage());
    }
    AppointmentDTO resultDto = appointmentService.createOnlineAppointment(dto);
    return ResponseEntity.ok(resultDto);
  }

  @PostMapping("/anonymous-online")
  public ResponseEntity<AppointmentDTO> createAnonymousOnlineAppointment(
      @RequestBody AnonymousOnlineDTO dto) {
    AppointmentDTO result = appointmentService.createAnonymousOnlineAppointment(dto);
    return ResponseEntity.ok(result);
  }
}