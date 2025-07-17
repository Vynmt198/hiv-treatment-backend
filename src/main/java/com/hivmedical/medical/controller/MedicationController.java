package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.MedicationDTO;
import com.hivmedical.medical.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    // Lấy tất cả thuốc đang hoạt động (cho tất cả người dùng)
    @GetMapping("/active")
    public ResponseEntity<List<MedicationDTO>> getAllActiveMedications() {
        List<MedicationDTO> medications = medicationService.getAllActiveMedications();
        return ResponseEntity.ok(medications);
    }

    // Lấy tất cả thuốc (chỉ Admin/Doctor)
    @GetMapping
    public ResponseEntity<List<MedicationDTO>> getAllMedications() {
        List<MedicationDTO> medications = medicationService.getAllMedications();
        return ResponseEntity.ok(medications);
    }

    // Lấy thuốc theo ID
    @GetMapping("/{id}")
    public ResponseEntity<MedicationDTO> getMedicationById(@PathVariable Long id) {
        MedicationDTO medication = medicationService.getMedicationById(id);
        return ResponseEntity.ok(medication);
    }

    // Lấy thuốc theo nhóm thuốc
    @GetMapping("/drug-class/{drugClass}")
    public ResponseEntity<List<MedicationDTO>> getMedicationsByDrugClass(@PathVariable String drugClass) {
        List<MedicationDTO> medications = medicationService.getMedicationsByDrugClass(drugClass);
        return ResponseEntity.ok(medications);
    }

    // Tìm kiếm thuốc theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<List<MedicationDTO>> searchMedications(@RequestParam String keyword) {
        List<MedicationDTO> medications = medicationService.searchMedications(keyword);
        return ResponseEntity.ok(medications);
    }

    // Lấy tất cả nhóm thuốc
    @GetMapping("/drug-classes")
    public ResponseEntity<List<String>> getAllDrugClasses() {
        List<String> drugClasses = medicationService.getAllDrugClasses();
        return ResponseEntity.ok(drugClasses);
    }

    // Lấy thuốc theo tên
    @GetMapping("/name/{name}")
    public ResponseEntity<MedicationDTO> getMedicationByName(@PathVariable String name) {
        MedicationDTO medication = medicationService.getMedicationByName(name);
        return ResponseEntity.ok(medication);
    }

    // Lấy thuốc theo tên gốc
    @GetMapping("/generic-name/{genericName}")
    public ResponseEntity<MedicationDTO> getMedicationByGenericName(@PathVariable String genericName) {
        MedicationDTO medication = medicationService.getMedicationByGenericName(genericName);
        return ResponseEntity.ok(medication);
    }

    // Tạo thuốc mới (chỉ Admin/Doctor)
    @PostMapping
    public ResponseEntity<MedicationDTO> createMedication(@Valid @RequestBody MedicationDTO dto) {
        MedicationDTO createdMedication = medicationService.createMedication(dto);
        return ResponseEntity.ok(createdMedication);
    }

    // Cập nhật thuốc (chỉ Admin/Doctor)
    @PutMapping("/{id}")
    public ResponseEntity<MedicationDTO> updateMedication(@PathVariable Long id,
            @Valid @RequestBody MedicationDTO dto) {
        MedicationDTO updatedMedication = medicationService.updateMedication(id, dto);
        return ResponseEntity.ok(updatedMedication);
    }

    // Xóa thuốc (chỉ Admin/Doctor)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedication(@PathVariable Long id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.ok().build();
    }

    // Kích hoạt/phục hồi thuốc (chỉ Admin/Doctor)
    @PatchMapping("/{id}/activate")
    public ResponseEntity<MedicationDTO> activateMedication(@PathVariable Long id) {
        MedicationDTO activatedMedication = medicationService.activateMedication(id);
        return ResponseEntity.ok(activatedMedication);
    }
}