package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.hivmedical.medical.entitty.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByUserUsername(String username);

    List<AppointmentEntity> findByUserId(Long userId);

    List<AppointmentEntity> findByUserEmail(String email);

    List<AppointmentEntity> findByDoctorId(Long doctorId);

    List<AppointmentEntity> findByStatus(AppointmentStatus status);

    List<AppointmentEntity> findByUserUsernameAndStatus(String username, String status);
}