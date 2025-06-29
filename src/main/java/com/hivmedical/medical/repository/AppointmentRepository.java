package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.user.userId = :userId")
    List<Appointment> findByUserId(@Param("userId") Long userId);

    List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END " +
        "FROM appointments a " +
        "WHERE a.doctor_id = :doctorId AND a.date = :date AND a.time = CAST(:time AS TIME)",
        nativeQuery = true)
    boolean existsByDoctorIdAndDateAndTime(@Param("doctorId") Long doctorId,
        @Param("date") LocalDate date,
        @Param("time") LocalTime time);
}