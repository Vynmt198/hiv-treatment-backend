package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Account;
import com.hivmedical.medical.entitty.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    Optional<DoctorProfile> findByAccount(Account account);

    Optional<DoctorProfile> findByAccountId(Long accountId);

    Optional<DoctorProfile> findByAccountUsername(String username);

    Optional<DoctorProfile> findByAccountEmail(String email);

    @Query("SELECT dp FROM DoctorProfile dp WHERE dp.fullName LIKE %:keyword% OR dp.specialization LIKE %:keyword%")
    List<DoctorProfile> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT dp FROM DoctorProfile dp WHERE dp.specialization = :specialization")
    List<DoctorProfile> findBySpecialization(@Param("specialization") String specialization);

    @Query("SELECT dp FROM DoctorProfile dp WHERE dp.qualification = :qualification")
    List<DoctorProfile> findByQualification(@Param("qualification") String qualification);

    @Query("SELECT dp FROM DoctorProfile dp WHERE dp.specialization LIKE %:specialization%")
    List<DoctorProfile> findBySpecializationContaining(@Param("specialization") String specialization);
}