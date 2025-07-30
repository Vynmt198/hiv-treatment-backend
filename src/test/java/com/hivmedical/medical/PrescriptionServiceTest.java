package com.hivmedical.medical;

import com.hivmedical.medical.dto.PrescriptionDTO;
import com.hivmedical.medical.entitty.*;
import com.hivmedical.medical.repository.*;
import com.hivmedical.medical.service.PrescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrescriptionServiceTest {
    private PrescriptionRepository prescriptionRepository;
    private AccountRepository accountRepository;
    private DoctorRepository doctorRepository;
    private ARVProtocolRepository arvProtocolRepository;
    private AppointmentRepository appointmentRepository;

    private PrescriptionService prescriptionService;

    @BeforeEach
    void setup() {
        prescriptionRepository = mock(PrescriptionRepository.class);
        accountRepository = mock(AccountRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        arvProtocolRepository = mock(ARVProtocolRepository.class);
        appointmentRepository = mock(AppointmentRepository.class);

        prescriptionService = new PrescriptionService(
                prescriptionRepository,
                accountRepository,
                doctorRepository,
                arvProtocolRepository,
                appointmentRepository);
    }

    @Test
    void testConstructor_NotNull() {
        assertNotNull(prescriptionService);
    }

    @Test
    void testCreatePrescription_Success() {

        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(2L);
        dto.setProtocolId(3L);
        dto.setStartDate(LocalDate.now().toString());
        dto.setEndDate(LocalDate.now().plusDays(10).toString());

        Account patient = new Account();
        patient.setId(1L);
        Doctor doctor = new Doctor();
        doctor.setId(2L);
        ARVProtocol protocol = new ARVProtocol();
        protocol.setId(3L);
        Prescription savedPrescription = new Prescription();
        savedPrescription.setPatient(patient);
        savedPrescription.setDoctor(doctor);
        savedPrescription.setProtocol(protocol);
        savedPrescription.setStatus(PrescriptionStatus.ACTIVE);
        savedPrescription.setPrescribedDate(LocalDateTime.now());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(arvProtocolRepository.findById(3L)).thenReturn(Optional.of(protocol));
        when(prescriptionRepository.save(any())).thenReturn(savedPrescription);

        PrescriptionDTO result = prescriptionService.createPrescription(dto);

        assertNotNull(result);
        verify(prescriptionRepository, times(1)).save(any());
    }

    @Test
    void testCreatePrescription_MissingPatientId() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setDoctorId(2L);
        dto.setProtocolId(3L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            prescriptionService.createPrescription(dto);
        });

        assertEquals("ID bệnh nhân không được để trống", exception.getMessage());
    }

    @Test
    void testCreatePrescription_MissingDoctorId() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setPatientId(1L);
        dto.setProtocolId(3L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            prescriptionService.createPrescription(dto);
        });

        assertEquals("ID bác sĩ không được để trống", exception.getMessage());
    }

    @Test
    void testCreatePrescription_InvalidDoctorId() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(999L);
        dto.setProtocolId(3L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            prescriptionService.createPrescription(dto);
        });

        assertTrue(exception.getMessage().contains("Bác sĩ không tồn tại"));
    }

    @Test
    void testCreatePrescription_MissingProtocolId() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(2L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            prescriptionService.createPrescription(dto);
        });

        assertEquals("ID phác đồ ARV không được để trống", exception.getMessage());
    }

    @Test
    void testCreatePrescription_WithAppointmentId() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(2L);
        dto.setProtocolId(3L);
        dto.setAppointmentId(10L);

        Prescription prescription = new Prescription();
        Account patient = new Account();
        patient.setId(1L);
        prescription.setPatient(patient);
        Doctor doctor = new Doctor();
        doctor.setId(2L);
        prescription.setDoctor(doctor);
        ARVProtocol protocol = new ARVProtocol();
        protocol.setId(3L);
        prescription.setProtocol(protocol);
        prescription.setStatus(PrescriptionStatus.ACTIVE);
        prescription.setPrescribedDate(LocalDateTime.now());

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(arvProtocolRepository.findById(anyLong())).thenReturn(Optional.of(protocol));
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(new AppointmentEntity()));
        when(prescriptionRepository.save(any())).thenReturn(prescription);

        PrescriptionDTO result = prescriptionService.createPrescription(dto);
        assertNotNull(result);
    }

    @Test
    void testCreatePrescription_StartDateAndEndDate() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(2L);
        dto.setProtocolId(3L);
        dto.setStartDate("2025-07-20");
        dto.setEndDate("2025-08-01");

        Account patient = new Account();
        patient.setId(1L);
        Doctor doctor = new Doctor();
        doctor.setId(2L);
        ARVProtocol protocol = new ARVProtocol();
        protocol.setId(3L);

        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setProtocol(protocol);
        prescription.setStatus(PrescriptionStatus.ACTIVE);
        prescription.setPrescribedDate(LocalDateTime.now());

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(arvProtocolRepository.findById(anyLong())).thenReturn(Optional.of(protocol));
        when(prescriptionRepository.save(any())).thenReturn(prescription);

        PrescriptionDTO result = prescriptionService.createPrescription(dto);
        assertNotNull(result);
    }

}
