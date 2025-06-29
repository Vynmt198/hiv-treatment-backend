package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.AppointmentDTO;
import com.hivmedical.medical.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

  private final AppointmentService appointmentService;

  public AppointmentController(AppointmentService appointmentService) {
    this.appointmentService = appointmentService;
  }

  // ✅ Đặt lịch: dùng userId nếu ẩn danh, hoặc lấy từ token nếu đã đăng nhập
  @PostMapping
  public ResponseEntity<Map<String, Object>> createAppointment(@Valid @RequestBody AppointmentDTO request,
      Authentication authentication) {
    try {
      AppointmentDTO appointment;

      if (!request.isAnonymous()) {
        if (authentication == null || !authentication.isAuthenticated()) {
          return ResponseEntity.status(401).body(Map.of(
              "success", false,
              "message", "Bạn cần đăng nhập để đặt lịch không ẩn danh."
          ));
        }
        String username = authentication.getName();
        appointment = appointmentService.createAppointmentWithUsername(username, request);
      } else {
        // Người dùng ẩn danh (anonymous)
        appointment = appointmentService.createAppointment(request);
      }

      return ResponseEntity.ok(Map.of(
          "success", true,
          "message", "Đặt lịch khám thành công",
          "data", appointment
      ));

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
          "success", false,
          "message", "Lỗi đặt lịch khám: " + e.getMessage()
      ));
    }
  }

  // ✅ Lấy lịch sử khám của user hiện tại (dựa vào JWT)
  @GetMapping("/me")
  public ResponseEntity<Map<String, Object>> getMyAppointments(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).body(Map.of(
          "success", false,
          "message", "Bạn cần đăng nhập để xem lịch của mình."
      ));
    }

    try {
      String username = authentication.getName();
      List<AppointmentDTO> appointments = appointmentService.getAppointmentsByUsername(username);
      return ResponseEntity.ok(Map.of(
          "success", true,
          "message", "Lịch khám của bạn",
          "data", appointments
      ));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
          "success", false,
          "message", "Không thể lấy lịch khám: " + e.getMessage()
      ));
    }
  }

  // ✅ Admin hoặc bác sĩ có thể lấy lịch của bệnh nhân cụ thể
  @GetMapping("/patient/{userId}")
  public ResponseEntity<Map<String, Object>> getUserAppointments(@PathVariable Long userId) {
    try {
      List<AppointmentDTO> appointments = appointmentService.getUserAppointments(userId);
      return ResponseEntity.ok(Map.of(
          "success", true,
          "message", "Lịch khám theo user ID",
          "data", appointments
      ));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
          "success", false,
          "message", "Không thể lấy lịch khám: " + e.getMessage()
      ));
    }
  }
}
