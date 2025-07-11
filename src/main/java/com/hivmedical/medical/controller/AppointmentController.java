package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.OnlineAppointmentDTO;
import com.hivmedical.medical.dto.AnonymousOnlineDTO;
import com.hivmedical.medical.service.AppointmentService;
import com.hivmedical.medical.entitty.AppointmentStatus;
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
  @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
  public ResponseEntity<List<AppointmentDTO>> getAppointments(
      @RequestParam(value = "status", required = false) String status) {
    if (status != null && !status.isEmpty()) {
      AppointmentStatus enumStatus;
      try {
        enumStatus = AppointmentStatus.valueOf(status);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
      }
      return ResponseEntity.ok(appointmentService.getAppointmentsByStatus(enumStatus));
    } else {
      return ResponseEntity.ok(appointmentService.getAllAppointments());
    }
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
  public ResponseEntity<AppointmentDTO> updateAppointmentStatus(@PathVariable Long id,
      @RequestBody AppointmentDTO dto) {
    if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
      throw new IllegalArgumentException("Trạng thái không được để trống");
    }
    AppointmentStatus enumStatus;
    try {
      enumStatus = AppointmentStatus.valueOf(dto.getStatus());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Trạng thái không hợp lệ: " + dto.getStatus());
    }
    AppointmentDTO updated = appointmentService.updateAppointmentStatus(id, enumStatus);
    return ResponseEntity.ok(updated);
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