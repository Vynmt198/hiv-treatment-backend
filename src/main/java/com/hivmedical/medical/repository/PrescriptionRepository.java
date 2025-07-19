package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Prescription;
import com.hivmedical.medical.entitty.PrescriptionStatus;
import com.hivmedical.medical.entitty.Account;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatient(Account patient);

    List<Prescription> findByPatientAndStatus(Account patient, PrescriptionStatus status);

    List<Prescription> findByDoctor(Doctor doctor);

    List<Prescription> findByDoctorAndStatus(Doctor doctor, PrescriptionStatus status);

    List<Prescription> findByStatus(PrescriptionStatus status);

    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.status = 'ACTIVE' ORDER BY p.prescribedDate DESC")
    List<Prescription> findActivePrescriptionsByPatient(@Param("patient") Account patient);

    @Query("SELECT p FROM Prescription p WHERE p.doctor = :doctor AND p.status = 'ACTIVE' ORDER BY p.prescribedDate DESC")
    List<Prescription> findActivePrescriptionsByDoctor(@Param("doctor") Doctor doctor);

    @Query("SELECT p FROM Prescription p WHERE p.prescribedDate BETWEEN :startDate AND :endDate")
    List<Prescription> findByPrescribedDateBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.prescribedDate >= :startDate ORDER BY p.prescribedDate DESC")
    List<Prescription> findRecentPrescriptionsByPatient(@Param("patient") Account patient,
            @Param("startDate") LocalDateTime startDate);

    List<Prescription> findByAppointment(AppointmentEntity appointment);

    List<Prescription> findByAppointmentAndStatus(AppointmentEntity appointment, PrescriptionStatus status);
}