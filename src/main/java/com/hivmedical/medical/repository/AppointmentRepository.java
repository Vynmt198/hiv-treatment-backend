package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.hivmedical.medical.entitty.AppointmentStatus;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByUserUsername(String username);

    List<AppointmentEntity> findByUserId(Long userId);

    List<AppointmentEntity> findByUserEmail(String email);

    List<AppointmentEntity> findByDoctorId(Long doctorId);

    List<AppointmentEntity> findByStatus(AppointmentStatus status);

    List<AppointmentEntity> findByUserUsernameAndStatus(String username, String status);

    AppointmentEntity findByScheduleId(Long scheduleId);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.status IN :statuses AND a.createdAt < :threshold")
    List<AppointmentEntity> findByStatusInAndCreatedAtBefore(@Param("statuses") List<AppointmentStatus> statuses,
            @Param("threshold") LocalDateTime threshold);

    long countByStatus(AppointmentStatus status);
}