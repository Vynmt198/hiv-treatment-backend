package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.*;
import com.hivmedical.medical.repository.*;
import com.hivmedical.medical.dto.TestResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestResultService {
    @Autowired
    private TestResultRepository testResultRepository;
    @Autowired
    private TestCategoryRepository testCategoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    private TestResultDTO toDTO(TestResult entity) {
        TestResultDTO dto = new TestResultDTO();
        dto.setId(entity.getId());
        if (entity.getPatient() != null) {
            dto.setPatientId(entity.getPatient().getId());
            dto.setPatientName(entity.getPatient().getUsername()); // hoặc lấy tên thật nếu có
        }
        if (entity.getDoctor() != null) {
            dto.setDoctorId(entity.getDoctor().getId());
            dto.setDoctorName(entity.getDoctor().getFullName()); // hoặc getUsername nếu không có fullName
        }
        if (entity.getTestCategory() != null) {
            dto.setTestCategoryId(entity.getTestCategory().getId());
            dto.setTestCategoryName(entity.getTestCategory().getName());
        }
        if (entity.getAppointment() != null) {
            dto.setAppointmentId(entity.getAppointment().getId());
        }
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setResultValue(entity.getResultValue());
        dto.setResultNote(entity.getResultNote());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setResultDate(entity.getResultDate());
        return dto;
    }

    public List<TestResultDTO> getAllTestResults() {
        return testResultRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TestResult createTestRequest(Long patientId, Long testCategoryId, Long doctorId, Long appointmentId,
            String note) {
        TestResult testResult = new TestResult();
        testResult.setPatient(accountRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found")));
        testResult.setTestCategory(testCategoryRepository.findById(testCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Test category not found")));
        if (doctorId != null) {
            testResult.setDoctor(doctorRepository.findById(doctorId).orElse(null));
        }
        if (appointmentId != null) {
            testResult.setAppointment(appointmentRepository.findById(appointmentId).orElse(null));
        }
        testResult.setStatus(TestStatus.REQUESTED);
        testResult.setResultNote(note);
        testResult.setCreatedAt(LocalDateTime.now());
        testResult.setUpdatedAt(LocalDateTime.now());
        return testResultRepository.save(testResult);
    }

    public TestResultDTO createTestRequestDTO(Long patientId, Long testCategoryId, Long doctorId, Long appointmentId,
            String note) {
        return toDTO(createTestRequest(patientId, testCategoryId, doctorId, appointmentId, note));
    }

    @Transactional
    public TestResult updateTestStatus(Long testResultId, TestStatus status) {
        TestResult testResult = testResultRepository.findById(testResultId)
                .orElseThrow(() -> new IllegalArgumentException("Test result not found"));
        testResult.setStatus(status);
        testResult.setUpdatedAt(LocalDateTime.now());
        return testResultRepository.save(testResult);
    }

    public TestResultDTO updateTestStatusDTO(Long testResultId, TestStatus status) {
        return toDTO(updateTestStatus(testResultId, status));
    }

    @Transactional
    public TestResult updateTestResult(Long testResultId, String resultValue, String resultNote) {
        TestResult testResult = testResultRepository.findById(testResultId)
                .orElseThrow(() -> new IllegalArgumentException("Test result not found"));
        testResult.setResultValue(resultValue);
        testResult.setResultNote(resultNote);
        testResult.setStatus(TestStatus.COMPLETED);
        testResult.setResultDate(LocalDateTime.now());
        testResult.setUpdatedAt(LocalDateTime.now());
        return testResultRepository.save(testResult);
    }

    public TestResultDTO updateTestResultDTO(Long testResultId, String resultValue, String resultNote) {
        return toDTO(updateTestResult(testResultId, resultValue, resultNote));
    }

    public List<TestResult> getResultsByPatient(Long patientId) {
        return testResultRepository.findByPatientId(patientId);
    }

    public List<TestResultDTO> getResultsByPatientDTO(Long patientId) {
        return getResultsByPatient(patientId).stream().map(this::toDTO).toList();
    }

    public List<TestResult> getResultsByDoctor(Long doctorId) {
        return testResultRepository.findByDoctor(doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found")));
    }

    public List<TestResultDTO> getResultsByDoctorDTO(Long doctorId) {
        return getResultsByDoctor(doctorId).stream().map(this::toDTO).toList();
    }

    public List<TestResult> getResultsByStatus(TestStatus status) {
        return testResultRepository.findByStatus(status);
    }

    public List<TestResultDTO> getResultsByStatusDTO(TestStatus status) {
        return getResultsByStatus(status).stream().map(this::toDTO).toList();
    }

    public TestResult getTestResult(Long id) {
        return testResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test result not found"));
    }

    public TestResultDTO getTestResultDTO(Long id) {
        return toDTO(getTestResult(id));
    }
}