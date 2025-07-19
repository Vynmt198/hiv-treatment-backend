package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.PrescriptionDTO;
import com.hivmedical.medical.dto.StatusUpdateRequest;
import com.hivmedical.medical.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // Lấy tất cả đơn thuốc (chỉ Admin/Doctor/Staff)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    public ResponseEntity<List<PrescriptionDTO>> getAllPrescriptions() {
        List<PrescriptionDTO> prescriptions = prescriptionService.getAllPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    // Lấy đơn thuốc theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable Long id) {
        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(prescription);
    }

    // Lấy đơn thuốc của bệnh nhân (bệnh nhân chỉ xem được của mình)
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByPatient(@PathVariable Long patientId) {
        System.out.println("=== DEBUG: Controller called with patientId: " + patientId);
        try {
            List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
            System.out.println("=== DEBUG: Controller returning " + prescriptions.size() + " prescriptions");
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            System.err.println("=== ERROR: Controller error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Lấy đơn thuốc đang hoạt động của bệnh nhân
    @GetMapping("/patient/{patientId}/active")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<PrescriptionDTO>> getActivePrescriptionsByPatient(@PathVariable Long patientId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getActivePrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    // Lấy đơn thuốc theo appointment
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByAppointment(@PathVariable Long appointmentId) {
        System.out.println("=== DEBUG: Controller called with appointmentId: " + appointmentId);
        try {
            List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByAppointment(appointmentId);
            System.out.println("=== DEBUG: Controller returning " + prescriptions.size() + " prescriptions");
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            System.err.println("=== ERROR: Controller error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Lấy đơn thuốc của bác sĩ
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByDoctor(@PathVariable Long doctorId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
        return ResponseEntity.ok(prescriptions);
    }

    // Lấy đơn thuốc đang hoạt động của bác sĩ
    @GetMapping("/doctor/{doctorId}/active")
    @PreAuthorize("hasAnyRole('DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<PrescriptionDTO>> getActivePrescriptionsByDoctor(@PathVariable Long doctorId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getActivePrescriptionsByDoctor(doctorId);
        return ResponseEntity.ok(prescriptions);
    }

    // Lấy đơn thuốc theo trạng thái
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByStatus(@PathVariable String status) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByStatus(status);
        return ResponseEntity.ok(prescriptions);
    }

    // Tạo đơn thuốc mới (chỉ Doctor/Admin)
    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PrescriptionDTO> createPrescription(@Valid @RequestBody PrescriptionDTO dto) {
        PrescriptionDTO createdPrescription = prescriptionService.createPrescription(dto);
        return ResponseEntity.ok(createdPrescription);
    }

    // Cập nhật đơn thuốc (chỉ Doctor/Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PrescriptionDTO> updatePrescription(@PathVariable Long id,
            @Valid @RequestBody PrescriptionDTO dto) {
        PrescriptionDTO updatedPrescription = prescriptionService.updatePrescription(id, dto);
        return ResponseEntity.ok(updatedPrescription);
    }

    // Cập nhật trạng thái đơn thuốc (chỉ Doctor/Admin)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PrescriptionDTO> updatePrescriptionStatus(@PathVariable Long id,
            @RequestParam String status) {
        PrescriptionDTO updatedPrescription = prescriptionService.updatePrescriptionStatus(id, status);
        return ResponseEntity.ok(updatedPrescription);
    }

    // Cập nhật trạng thái đơn thuốc (PUT method)/không dùng
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PrescriptionDTO> updatePrescriptionStatusPut(@PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        PrescriptionDTO updatedPrescription = prescriptionService.updatePrescriptionStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedPrescription);
    }

    // Dừng đơn thuốc (chỉ Doctor/Admin)
    @PatchMapping("/{id}/discontinue")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PrescriptionDTO> discontinuePrescription(@PathVariable Long id, @RequestParam String reason) {
        PrescriptionDTO discontinuedPrescription = prescriptionService.discontinuePrescription(id, reason);
        return ResponseEntity.ok(discontinuedPrescription);
    }

    // Endpoint test để kiểm tra database
    @GetMapping("/test/database")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Kiểm tra số lượng prescriptions
            List<PrescriptionDTO> allPrescriptions = prescriptionService.getAllPrescriptions();
            result.put("totalPrescriptions", allPrescriptions.size());
            result.put("prescriptions", allPrescriptions);

            // Kiểm tra prescriptions cho patient 8
            try {
                List<PrescriptionDTO> patient8Prescriptions = prescriptionService.getPrescriptionsByPatient(8L);
                result.put("patient8Prescriptions", patient8Prescriptions);
                result.put("patient8Count", patient8Prescriptions.size());
            } catch (Exception e) {
                result.put("patient8Error", e.getMessage());
            }

            // Kiểm tra prescriptions cho appointment 134
            try {
                List<PrescriptionDTO> appointment134Prescriptions = prescriptionService
                        .getPrescriptionsByAppointment(134L);
                result.put("appointment134Prescriptions", appointment134Prescriptions);
                result.put("appointment134Count", appointment134Prescriptions.size());
            } catch (Exception e) {
                result.put("appointment134Error", e.getMessage());
            }

            result.put("status", "success");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(result);
    }

    // Tạm ngưng đơn thuốc (chỉ Doctor/Admin)
    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PrescriptionDTO> suspendPrescription(@PathVariable Long id, @RequestParam String reason) {
        PrescriptionDTO suspendedPrescription = prescriptionService.suspendPrescription(id, reason);
        return ResponseEntity.ok(suspendedPrescription);
    }
}