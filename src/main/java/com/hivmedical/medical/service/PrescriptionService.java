package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.PrescriptionDTO;
import com.hivmedical.medical.entitty.*;
import com.hivmedical.medical.repository.PrescriptionRepository;
import com.hivmedical.medical.repository.AccountRepository;
import com.hivmedical.medical.repository.DoctorRepository;
import com.hivmedical.medical.repository.ARVProtocolRepository;
import com.hivmedical.medical.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ARVProtocolRepository arvProtocolRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Lấy tất cả đơn thuốc
    public List<PrescriptionDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy đơn thuốc theo ID
    public PrescriptionDTO getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn thuốc không tồn tại với ID: " + id));
        return mapToDTO(prescription);
    }

    // Lấy đơn thuốc của bệnh nhân
    public List<PrescriptionDTO> getPrescriptionsByPatient(Long patientId) {
        System.out.println("=== DEBUG: getPrescriptionsByPatient called with patientId: " + patientId);

        Account patient = accountRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Bệnh nhân không tồn tại với ID: " + patientId));

        List<Prescription> prescriptions = prescriptionRepository.findByPatient(patient);
        System.out.println("=== DEBUG: Found " + prescriptions.size() + " prescriptions for patient " + patientId);

        return prescriptions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy đơn thuốc đang hoạt động của bệnh nhân
    public List<PrescriptionDTO> getActivePrescriptionsByPatient(Long patientId) {
        Account patient = accountRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Bệnh nhân không tồn tại với ID: " + patientId));

        return prescriptionRepository.findActivePrescriptionsByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy đơn thuốc theo appointment
    public List<PrescriptionDTO> getPrescriptionsByAppointment(Long appointmentId) {
        System.out.println("=== DEBUG: getPrescriptionsByAppointment called with appointmentId: " + appointmentId);

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Lịch hẹn không tồn tại với ID: " + appointmentId));

        List<Prescription> prescriptions = prescriptionRepository.findByAppointment(appointment);
        System.out.println(
                "=== DEBUG: Found " + prescriptions.size() + " prescriptions for appointment " + appointmentId);

        return prescriptions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy đơn thuốc của bác sĩ
    public List<PrescriptionDTO> getPrescriptionsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại với ID: " + doctorId));

        return prescriptionRepository.findByDoctor(doctor)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy đơn thuốc đang hoạt động của bác sĩ
    public List<PrescriptionDTO> getActivePrescriptionsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại với ID: " + doctorId));

        return prescriptionRepository.findActivePrescriptionsByDoctor(doctor)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy đơn thuốc theo trạng thái
    public List<PrescriptionDTO> getPrescriptionsByStatus(String status) {
        PrescriptionStatus prescriptionStatus = PrescriptionStatus.valueOf(status.toUpperCase());
        return prescriptionRepository.findByStatus(prescriptionStatus)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Tạo đơn thuốc mới
    @Transactional
    public PrescriptionDTO createPrescription(PrescriptionDTO dto) {
        validatePrescriptionDTO(dto);

        Account patient = accountRepository.findById(dto.getPatientId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Bệnh nhân không tồn tại với ID: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại với ID: " + dto.getDoctorId()));

        ARVProtocol protocol = arvProtocolRepository.findById(dto.getProtocolId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Phác đồ ARV không tồn tại với ID: " + dto.getProtocolId()));

        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setProtocol(protocol);
        prescription.setCustomInstructions(dto.getCustomInstructions());
        prescription.setDosageAdjustments(dto.getDosageAdjustments());
        prescription.setNotes(dto.getNotes());
        prescription.setStatus(PrescriptionStatus.ACTIVE);
        prescription.setPrescribedDate(LocalDateTime.now());

        // Convert string dates to LocalDateTime
        if (dto.getStartDate() != null) {
            LocalDate startDate = LocalDate.parse(dto.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            prescription.setStartDate(startDate.atStartOfDay());
        }
        if (dto.getEndDate() != null) {
            LocalDate endDate = LocalDate.parse(dto.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            prescription.setEndDate(endDate.atStartOfDay());
        }

        // Liên kết với appointment nếu có
        if (dto.getAppointmentId() != null) {
            AppointmentEntity appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElse(null);
            prescription.setAppointment(appointment);
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return mapToDTO(savedPrescription);
    }

    // Cập nhật đơn thuốc
    @Transactional
    public PrescriptionDTO updatePrescription(Long id, PrescriptionDTO dto) {
        validatePrescriptionDTO(dto);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn thuốc không tồn tại với ID: " + id));

        prescription.setCustomInstructions(dto.getCustomInstructions());
        prescription.setDosageAdjustments(dto.getDosageAdjustments());
        prescription.setNotes(dto.getNotes());

        // Convert string dates to LocalDateTime
        if (dto.getStartDate() != null) {
            LocalDate startDate = LocalDate.parse(dto.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            prescription.setStartDate(startDate.atStartOfDay());
        }
        if (dto.getEndDate() != null) {
            LocalDate endDate = LocalDate.parse(dto.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            prescription.setEndDate(endDate.atStartOfDay());
        }

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return mapToDTO(updatedPrescription);
    }

    // Cập nhật trạng thái đơn thuốc
    @Transactional
    public PrescriptionDTO updatePrescriptionStatus(Long id, String status) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn thuốc không tồn tại với ID: " + id));

        PrescriptionStatus prescriptionStatus = PrescriptionStatus.valueOf(status.toUpperCase());
        prescription.setStatus(prescriptionStatus);

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return mapToDTO(updatedPrescription);
    }

    // Dừng đơn thuốc
    @Transactional
    public PrescriptionDTO discontinuePrescription(Long id, String reason) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn thuốc không tồn tại với ID: " + id));

        prescription.setStatus(PrescriptionStatus.DISCONTINUED);
        prescription.setNotes(prescription.getNotes() + "\nNgừng điều trị: " + reason);

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return mapToDTO(updatedPrescription);
    }

    // Tạm ngưng đơn thuốc
    @Transactional
    public PrescriptionDTO suspendPrescription(Long id, String reason) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn thuốc không tồn tại với ID: " + id));

        prescription.setStatus(PrescriptionStatus.SUSPENDED);
        prescription.setNotes(prescription.getNotes() + "\nTạm ngưng: " + reason);

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return mapToDTO(updatedPrescription);
    }

    // Chuyển đổi Entity sang DTO
    private PrescriptionDTO mapToDTO(Prescription prescription) {
        try {
            System.out.println("=== DEBUG: Mapping prescription ID: " + prescription.getId());

            PrescriptionDTO dto = new PrescriptionDTO();
            dto.setId(prescription.getId());
            dto.setPatientId(prescription.getPatient().getId());
            dto.setPatientName(prescription.getPatient().getUsername());
            dto.setDoctorId(prescription.getDoctor().getId());
            dto.setDoctorName(prescription.getDoctor().getFullName());
            dto.setProtocolId(prescription.getProtocol().getId());
            dto.setProtocolName(prescription.getProtocol().getName());
            dto.setCustomInstructions(prescription.getCustomInstructions());
            dto.setDosageAdjustments(prescription.getDosageAdjustments());
            dto.setNotes(prescription.getNotes());
            dto.setStatus(prescription.getStatus().name());
            dto.setPrescribedDate(prescription.getPrescribedDate());

            // Convert LocalDateTime to string dates
            if (prescription.getStartDate() != null) {
                dto.setStartDate(prescription.getStartDate().toLocalDate().toString());
            }
            if (prescription.getEndDate() != null) {
                dto.setEndDate(prescription.getEndDate().toLocalDate().toString());
            }

            dto.setCreatedAt(prescription.getCreatedAt());
            dto.setUpdatedAt(prescription.getUpdatedAt());

            if (prescription.getAppointment() != null) {
                dto.setAppointmentId(prescription.getAppointment().getId());
            }

            System.out.println("=== DEBUG: Successfully mapped prescription ID: " + prescription.getId());
            return dto;
        } catch (Exception e) {
            System.err.println("=== ERROR: Failed to map prescription ID: " + prescription.getId());
            System.err.println("=== ERROR: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Validate DTO
    private void validatePrescriptionDTO(PrescriptionDTO dto) {
        if (dto.getPatientId() == null) {
            throw new IllegalArgumentException("ID bệnh nhân không được để trống");
        }
        if (dto.getDoctorId() == null) {
            throw new IllegalArgumentException("ID bác sĩ không được để trống");
        }
        if (dto.getProtocolId() == null) {
            throw new IllegalArgumentException("ID phác đồ ARV không được để trống");
        }
    }
}