package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
  Page<Doctor> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);

  @Query("SELECT d FROM Doctor d WHERE LOWER(d.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
  Page<Doctor> findByNameContainingIgnoreCase(String name, Pageable pageable);

  Optional<Doctor> findByEmail(String email);

  Optional<Doctor> findByAccountId(Long accountId);

  Optional<Doctor> findByAccountUsername(String username);

  List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
}
