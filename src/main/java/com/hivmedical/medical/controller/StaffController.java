package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.PatientRegisterByStaffRequest;
import com.hivmedical.medical.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
  @Autowired
  private AccountService accountService;

  @GetMapping("/appointments")
  @PreAuthorize("hasRole('STAFF')")
  public ResponseEntity<String> getAppointments() {
    return ResponseEntity.ok("List of appointments for staff");
  }

  /**
   * Gán quyền DOCTOR cho account dựa trên doctorId
   * 
   * @param doctorId id của doctor (liên kết với account)
   * @return Thông báo thành công/thất bại
   */
  @PostMapping("/assign-doctor-role")
  @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
  public ResponseEntity<String> assignDoctorRole(@RequestParam Long doctorId) {
    boolean result = accountService.assignDoctorRoleByDoctorId(doctorId);
    if (result) {
      return ResponseEntity.ok("Cấp quyền bác sĩ thành công cho doctorId: " + doctorId);
    } else {
      return ResponseEntity.badRequest().body("Không tìm thấy account liên kết với doctorId: " + doctorId);
    }
  }

  @PostMapping("/patients")
  @PreAuthorize("hasRole('STAFF')")
  public ResponseEntity<?> createPatientByStaff(@RequestBody PatientRegisterByStaffRequest request) {
    try {
      boolean result = accountService.createPatientByStaff(request);
      if (result) {
        return ResponseEntity.ok("Thêm bệnh nhân thành công");
      } else {
        return ResponseEntity.badRequest().body("Thêm bệnh nhân thất bại");
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
    }
  }

}
