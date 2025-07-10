package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.PatientTreatmentPlan;
import com.hivmedical.medical.entitty.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientTreatmentPlanRepository extends JpaRepository<PatientTreatmentPlan, Long> {
    List<PatientTreatmentPlan> findByDoctorId(Long doctorId);
    List<PatientTreatmentPlan> findByPatient(UserEntity patient);
}