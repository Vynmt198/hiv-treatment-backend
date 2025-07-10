package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.TreatmentPlanTemplateDTO;
import com.hivmedical.medical.dto.PatientTreatmentPlanDTO;
import com.hivmedical.medical.service.TreatmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/doctor/treatment-plans")
public class TreatmentPlanController {
    private final TreatmentPlanService treatmentPlanService;

    @Autowired
    public TreatmentPlanController(TreatmentPlanService treatmentPlanService) {
        this.treatmentPlanService = treatmentPlanService;
    }

    @PostMapping("/templates")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<TreatmentPlanTemplateDTO> createTreatmentPlanTemplate(@Valid @RequestBody TreatmentPlanTemplateDTO dto) {
        return ResponseEntity.ok(treatmentPlanService.createTreatmentPlanTemplate(dto));
    }

    @PostMapping("/patient-treatment-plans")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createPatientTreatmentPlan(@Valid @RequestBody PatientTreatmentPlanDTO dto) {
        try {
            PatientTreatmentPlanDTO response = treatmentPlanService.createPatientTreatmentPlan(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/templates")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<TreatmentPlanTemplateDTO>> getTreatmentPlanTemplates() {
        return ResponseEntity.ok(treatmentPlanService.getTreatmentPlanTemplates());
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<PatientTreatmentPlanDTO>> getAssignedTreatmentPlansByDoctor() {
        return ResponseEntity.ok(treatmentPlanService.getAssignedTreatmentPlansByDoctor());
    }

    @GetMapping("/templates/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<TreatmentPlanTemplateDTO> getTreatmentPlanTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentPlanService.getTreatmentPlanTemplateById(id));
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<TreatmentPlanTemplateDTO> updateTreatmentPlanTemplate(@PathVariable Long id, @Valid @RequestBody TreatmentPlanTemplateDTO dto) {
        return ResponseEntity.ok(treatmentPlanService.updateTreatmentPlanTemplate(id, dto));
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteTreatmentPlanTemplate(@PathVariable Long id) {
        treatmentPlanService.deleteTreatmentPlanTemplate(id);
        return ResponseEntity.noContent().build();
    }
}