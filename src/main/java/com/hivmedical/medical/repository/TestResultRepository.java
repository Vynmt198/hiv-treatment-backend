package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.TestResult;
import com.hivmedical.medical.entitty.Account;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByPatient(Account patient);

    List<TestResult> findByDoctor(Doctor doctor);

    List<TestResult> findByPatientId(Long patientId);

    List<TestResult> findByStatus(TestStatus status);

    List<TestResult> findByPatientIdAndStatus(Long patientId, TestStatus status);
}