package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.dto.OnlineAppointmentDTO;
import com.hivmedical.medical.dto.AnonymousOnlineDTO;
import com.hivmedical.medical.service.AppointmentService;
import com.hivmedical.medical.service.MomoPaymentService;
import com.hivmedical.medical.entitty.AppointmentStatus;
import com.hivmedical.medical.entitty.ServiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.service.ScheduleService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

  @Autowired
  private AppointmentService appointmentService;

  @Autowired
  private MomoPaymentService momoPaymentService;

  @Autowired
  private ScheduleService scheduleService;

  @PostMapping
  @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
  public ResponseEntity<Map<String, Object>> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO,
      BindingResult result) {
    if (result.hasErrors()) {
      Map<String, Object> error = new HashMap<>();
      error.put("error", result.getAllErrors().get(0).getDefaultMessage());
      return ResponseEntity.badRequest().body(error);
    }

    AppointmentDTO savedAppointment = appointmentService.createAppointment(appointmentDTO);
    String url = momoPaymentService.getPayUrl(
        savedAppointment.getId().toString(),
        savedAppointment.getPrice(),
        "Thanh Toan Don Hang",
        "url",
        "url");

    Map<String, Object> response = new HashMap<>();
    response.put("appointment", savedAppointment);
    response.put("paymentUrl", url);

    return ResponseEntity.ok(response);
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
  public ResponseEntity<Map<String, String>> createOnlineAppointment(
      @Valid @RequestBody OnlineAppointmentDTO dto,
      BindingResult result) {

    if (result.hasErrors()) {
      throw new IllegalArgumentException(result.getAllErrors().get(0).getDefaultMessage());
    }

    if (dto.getServiceId() == null) {
      throw new IllegalArgumentException("Cần chọn dịch vụ");
    }

    // Kiểm tra service phải là ONLINE
    ServiceEntity service = appointmentService.getServiceById(dto.getServiceId());
    if (!"ONLINE".equalsIgnoreCase(service.getType())) {
      throw new IllegalArgumentException("Chỉ được chọn dịch vụ online cho lịch hẹn online!");
    }

    if (dto.getDoctorId() == null) {
      throw new IllegalArgumentException("Cần chọn bác sĩ");
    }

    if (dto.getAppointmentDate() == null || dto.getAppointmentDate().isEmpty()) {
      throw new IllegalArgumentException("Cần chọn thời gian lịch hẹn");
    }

    AppointmentDTO resultDto = appointmentService.createOnlineAppointment(dto);
    String url = momoPaymentService.getPayUrl(
        resultDto.getId().toString(),
        resultDto.getPrice(),
        "Thanh Toan Don Hang",
        "url",
        "url");

    Map<String, String> response = new HashMap<>();
    response.put("appointmentId", resultDto.getId().toString());
    response.put("payUrl", url);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/anonymous-online")
  public ResponseEntity<Map<String, String>> createAnonymousOnlineAppointment(
      @RequestBody AnonymousOnlineDTO dto) {

    if (dto.getServiceId() == null) {
      throw new IllegalArgumentException("Cần chọn dịch vụ");
    }

    // Kiểm tra service phải là ONLINE
    ServiceEntity service = appointmentService.getServiceById(dto.getServiceId());
    if (!"ONLINE".equalsIgnoreCase(service.getType())) {
      throw new IllegalArgumentException("Chỉ được chọn dịch vụ online cho lịch hẹn online!");
    }

    if (dto.getDoctorId() == null) {
      throw new IllegalArgumentException("Cần chọn bác sĩ");
    }

    if (dto.getAppointmentDate() == null || dto.getAppointmentDate().isEmpty()) {
      throw new IllegalArgumentException("Cần chọn thời gian lịch hẹn");
    }

    AppointmentDTO result = appointmentService.createAnonymousOnlineAppointment(dto);
    String url = momoPaymentService.getPayUrl(
        result.getId().toString(),
        result.getPrice(),
        "Thanh Toan Don Hang",
        "url",
        "url");

    Map<String, String> response = new HashMap<>();
    response.put("appointmentId", result.getId().toString());
    response.put("payUrl", url);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/confirm-payment/{appointmentId}")
  public ResponseEntity<AppointmentDTO> confirmPayment(@PathVariable Long appointmentId) {
    AppointmentDTO updated = appointmentService.confirmPayment(appointmentId);
    return ResponseEntity.ok(updated);
  }

  // Lấy lịch hẹn theo doctorId
  @GetMapping("/doctor/{doctorId}")
  @PreAuthorize("hasAnyRole('DOCTOR', 'STAFF', 'ADMIN')")
  public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
    return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
  }
}