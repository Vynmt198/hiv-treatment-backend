package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.TreatmentPlanTemplateDTO;
import com.hivmedical.medical.dto.PatientTreatmentPlanDTO;
import com.hivmedical.medical.entitty.*;
import com.hivmedical.medical.repository.TreatmentPlanTemplateRepository;
import com.hivmedical.medical.repository.PatientTreatmentPlanRepository;
import com.hivmedical.medical.repository.UserRepositoty;
import com.hivmedical.medical.repository.DoctorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TreatmentPlanService {
    private static final Logger logger = LoggerFactory.getLogger(TreatmentPlanService.class);

    private final TreatmentPlanTemplateRepository treatmentPlanTemplateRepository;
    private final PatientTreatmentPlanRepository patientTreatmentPlanRepository;
    private final UserRepositoty userRepository;
    private final DoctorRepository doctorRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public TreatmentPlanService(TreatmentPlanTemplateRepository treatmentPlanTemplateRepository,
                                PatientTreatmentPlanRepository patientTreatmentPlanRepository,
                                UserRepositoty userRepository,
                                DoctorRepository doctorRepository,
                                ObjectMapper objectMapper) {
        this.treatmentPlanTemplateRepository = treatmentPlanTemplateRepository;
        this.patientTreatmentPlanRepository = patientTreatmentPlanRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.objectMapper = objectMapper;
    }

    public TreatmentPlanTemplateDTO createTreatmentPlanTemplate(TreatmentPlanTemplateDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + username));
        if (!user.getRole().equals(Role.DOCTOR)) {
            throw new IllegalArgumentException("Only doctors can create treatment plan templates");
        }

        Doctor doctor = doctorRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user ID: " + user.getUserId()));

        TreatmentPlanTemplate entity = new TreatmentPlanTemplate();
        entity.setName(dto.getName());
        try {
            if (dto.getDrugComponents() != null && !dto.getDrugComponents().isEmpty()) {
                objectMapper.readTree(dto.getDrugComponents());
                entity.setDrugComponents(dto.getDrugComponents());
            } else {
                entity.setDrugComponents(dto.getDrugComponents());
            }
        } catch (Exception e) {
            logger.warn("Invalid JSON format for drug components, treating as text: {}", dto.getDrugComponents());
            entity.setDrugComponents(dto.getDrugComponents());
        }
        entity.setDosage(dto.getDosage());
        entity.setIndications(dto.getIndications());
        entity.setSideEffects(dto.getSideEffects());
        entity.setMonitoringEffectiveness(dto.getMonitoringEffectiveness());
        entity.setEffectivenessEvaluation(dto.getEffectivenessEvaluation());
        entity.setStartDate(LocalDate.parse(dto.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        if (dto.getEndDate() != null) {
            entity.setEndDate(LocalDate.parse(dto.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        }
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        entity.setUser(user);
        entity.setCreatedBy(doctor);

        TreatmentPlanTemplate saved = treatmentPlanTemplateRepository.save(entity);
        return mapToTemplateDTO(saved);
    }
    public PatientTreatmentPlanDTO assignTreatmentPlanToPatient(PatientTreatmentPlanDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + username));
        if (!user.getRole().equals(Role.DOCTOR)) {
            throw new IllegalArgumentException("Only doctors can assign treatment plans");
        }

        UserEntity patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + dto.getPatientId()));
        Doctor doctor = doctorRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found for user ID: " + user.getUserId()));
        TreatmentPlanTemplate plan = treatmentPlanTemplateRepository.findById(dto.getTreatmentPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Treatment plan template not found: " + dto.getTreatmentPlanId()));

        PatientTreatmentPlan entity = new PatientTreatmentPlan();
        entity.setPatient(patient);
        entity.setDoctor(doctor);
        entity.setTreatmentPlan(plan);
        entity.setAssignedAt(LocalDateTime.now());

        PatientTreatmentPlan saved = patientTreatmentPlanRepository.save(entity);
        return mapToPatientTreatmentPlanDTO(saved);
    }
    public PatientTreatmentPlanDTO createPatientTreatmentPlan(PatientTreatmentPlanDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + username));
        if (!user.getRole().equals(Role.DOCTOR)) {
            throw new IllegalArgumentException("Only doctors can create patient treatment plans");
        }

        Doctor doctor = doctorRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user ID: " + user.getUserId()));
        UserEntity patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + dto.getPatientId()));
        TreatmentPlanTemplate treatmentPlan = treatmentPlanTemplateRepository.findById(dto.getTreatmentPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Treatment plan not found: " + dto.getTreatmentPlanId()));

        PatientTreatmentPlan entity = new PatientTreatmentPlan();
        entity.setPatient(patient);
        entity.setTreatmentPlan(treatmentPlan);
        entity.setDoctor(doctor);
        entity.setAssignedAt(LocalDateTime.now());

        if (dto.getTreatmentDate() != null && !dto.getTreatmentDate().isEmpty()) {
            try {
                entity.setTreatmentDate(LocalDateTime.parse(dto.getTreatmentDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid treatmentDate format. Use ISO_LOCAL_DATE_TIME (e.g., 2025-07-10T09:00:00).");
            }
        } else {
            entity.setTreatmentDate(entity.getAssignedAt());
        }

        entity.setMedicalRecord(dto.getMedicalRecord() != null ? dto.getMedicalRecord() : "");
        entity.setHealthcarePlan(dto.getHealthcarePlan() != null ? dto.getHealthcarePlan() : "");

        PatientTreatmentPlan saved = patientTreatmentPlanRepository.save(entity);
        return mapToPatientTreatmentPlanDTO(saved);
    }
    public List<TreatmentPlanTemplateDTO> getTreatmentPlanTemplates() {
        return treatmentPlanTemplateRepository.findAll()
                .stream()
                .map(this::mapToTemplateDTO)
                .collect(Collectors.toList());
    }

    public List<PatientTreatmentPlanDTO> getAssignedTreatmentPlansByDoctor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + username));
        Doctor doctor = doctorRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user ID: " + user.getUserId()));
        return patientTreatmentPlanRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(this::mapToPatientTreatmentPlanDTO)
                .collect(Collectors.toList());
    }

    public TreatmentPlanTemplateDTO getTreatmentPlanTemplateById(Long id) {
        TreatmentPlanTemplate plan = treatmentPlanTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treatment plan template not found: " + id));
        return mapToTemplateDTO(plan);
    }

    public TreatmentPlanTemplateDTO updateTreatmentPlanTemplate(Long id, TreatmentPlanTemplateDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + username));
        if (!user.getRole().equals(Role.DOCTOR)) {
            throw new IllegalArgumentException("Only doctors can update treatment plan templates");
        }

        TreatmentPlanTemplate plan = treatmentPlanTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treatment plan template not found: " + id));

        // Chỉ cập nhật nếu giá trị không null, giữ nguyên nếu không gửi
        if (dto.getName() != null) plan.setName(dto.getName());
        if (dto.getDrugComponents() != null && !dto.getDrugComponents().trim().isEmpty()) { // Chỉ cập nhật nếu có giá trị
            try {
                objectMapper.readTree(dto.getDrugComponents());
                plan.setDrugComponents(dto.getDrugComponents());
            } catch (Exception e) {
                logger.warn("Invalid JSON format for drug components, treating as text: {}", dto.getDrugComponents());
                plan.setDrugComponents(dto.getDrugComponents());
            }
        }
        if (dto.getDosage() != null && !dto.getDosage().trim().isEmpty()) plan.setDosage(dto.getDosage());
        if (dto.getIndications() != null && !dto.getIndications().trim().isEmpty()) plan.setIndications(dto.getIndications());
        if (dto.getSideEffects() != null && !dto.getSideEffects().trim().isEmpty()) plan.setSideEffects(dto.getSideEffects());
        if (dto.getMonitoringEffectiveness() != null && !dto.getMonitoringEffectiveness().trim().isEmpty()) plan.setMonitoringEffectiveness(dto.getMonitoringEffectiveness());
        if (dto.getEffectivenessEvaluation() != null && !dto.getEffectivenessEvaluation().trim().isEmpty()) plan.setEffectivenessEvaluation(dto.getEffectivenessEvaluation());
        if (dto.getStartDate() != null && !dto.getStartDate().trim().isEmpty()) {
            plan.setStartDate(LocalDate.parse(dto.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (dto.getEndDate() != null && !dto.getEndDate().trim().isEmpty()) {
            plan.setEndDate(LocalDate.parse(dto.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) plan.setStatus(dto.getStatus());

        TreatmentPlanTemplate updated = treatmentPlanTemplateRepository.save(plan);
        return mapToTemplateDTO(updated);
    }

    public void deleteTreatmentPlanTemplate(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + username));
        if (!user.getRole().equals(Role.DOCTOR)) {
            throw new IllegalArgumentException("Only doctors can delete treatment plan templates");
        }

        if (!treatmentPlanTemplateRepository.existsById(id)) {
            throw new IllegalArgumentException("Treatment plan template not found: " + id);
        }
        treatmentPlanTemplateRepository.deleteById(id);
    }

    private TreatmentPlanTemplateDTO mapToTemplateDTO(TreatmentPlanTemplate entity) {
        TreatmentPlanTemplateDTO dto = new TreatmentPlanTemplateDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDrugComponents(entity.getDrugComponents());
        dto.setDosage(entity.getDosage());
        dto.setIndications(entity.getIndications());
        dto.setSideEffects(entity.getSideEffects());
        dto.setMonitoringEffectiveness(entity.getMonitoringEffectiveness());
        dto.setEffectivenessEvaluation(entity.getEffectivenessEvaluation());
        dto.setStartDate(entity.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dto.setEndDate(entity.getEndDate() != null ? entity.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy().getId());
        return dto;
    }

    private PatientTreatmentPlanDTO mapToPatientTreatmentPlanDTO(PatientTreatmentPlan entity) {
        PatientTreatmentPlanDTO dto = new PatientTreatmentPlanDTO();
        if (entity != null) {
            dto.setId(entity.getId());
            dto.setPatientId(entity.getPatient() != null ? entity.getPatient().getUserId() : null);
            dto.setTreatmentPlanId(entity.getTreatmentPlan() != null ? entity.getTreatmentPlan().getId() : null);
            dto.setAssignedAt(entity.getAssignedAt() != null ? entity.getAssignedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
            dto.setTreatmentDate(entity.getTreatmentDate() != null ? entity.getTreatmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
            dto.setMedicalRecord(entity.getMedicalRecord());
            dto.setHealthcarePlan(entity.getHealthcarePlan());
        }
        return dto;
    }
}