package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.TestResultDTO;
import com.hivmedical.medical.entitty.TestStatus;
import com.hivmedical.medical.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-results")
public class TestResultController {
    @Autowired
    private TestResultService testResultService;

    // Staff/Admin: Lấy danh sách tất cả kết quả xét nghiệm
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<List<TestResultDTO>> getAllTestResults() {
        try {
            List<TestResultDTO> testResults = testResultService.getAllTestResults();
            return ResponseEntity.ok(testResults);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Staff: Tạo mới yêu cầu xét nghiệm
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<TestResultDTO> createTestRequest(@RequestParam Long patientId,
            @RequestParam Long testCategoryId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long appointmentId,
            @RequestParam(required = false) String note) {
        return ResponseEntity
                .ok(testResultService.createTestRequestDTO(patientId, testCategoryId, doctorId, appointmentId, note));
    }

    // Staff: Cập nhật trạng thái xét nghiệm
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<TestResultDTO> updateTestStatus(@PathVariable Long id, @RequestParam TestStatus status) {
        return ResponseEntity.ok(testResultService.updateTestStatusDTO(id, status));
    }

    // Staff: Nhập/cập nhật kết quả xét nghiệm
    @PatchMapping("/{id}/result")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<TestResultDTO> updateTestResult(@PathVariable Long id,
            @RequestParam String resultValue,
            @RequestParam(required = false) String resultNote) {
        return ResponseEntity.ok(testResultService.updateTestResultDTO(id, resultValue, resultNote));
    }

    // Bệnh nhân: Xem kết quả của mình
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<TestResultDTO>> getResultsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(testResultService.getResultsByPatientDTO(patientId));
    }

    // Bác sĩ: Xem kết quả của bệnh nhân mình phụ trách
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<TestResultDTO>> getResultsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(testResultService.getResultsByDoctorDTO(doctorId));
    }

    // Xem chi tiết kết quả xét nghiệm
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<TestResultDTO> getTestResult(@PathVariable Long id) {
        return ResponseEntity.ok(testResultService.getTestResultDTO(id));
    }

    // Lấy danh sách phân loại test results
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<com.hivmedical.medical.entitty.TestCategory>> getAllTestCategories() {
        return ResponseEntity.ok(testResultService.getAllTestCategories());
    }
}