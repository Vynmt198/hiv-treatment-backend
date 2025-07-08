package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Account;
import com.hivmedical.medical.entitty.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

    Optional<PatientProfile> findByAccount(Account account);

    Optional<PatientProfile> findByAccountId(Long accountId);

    Optional<PatientProfile> findByAccountUsername(String username);

    Optional<PatientProfile> findByAccountEmail(String email);

    @Query("SELECT pp FROM PatientProfile pp WHERE pp.fullName LIKE %:keyword% OR pp.phone LIKE %:keyword%")
    List<PatientProfile> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT pp FROM PatientProfile pp WHERE pp.hivStatus = :hivStatus")
    List<PatientProfile> findByHivStatus(@Param("hivStatus") String hivStatus);

    @Query("SELECT pp FROM PatientProfile pp WHERE pp.gender = :gender")
    List<PatientProfile> findByGender(@Param("gender") String gender);

    @Query("SELECT pp FROM PatientProfile pp WHERE pp.birthDate BETWEEN :startDate AND :endDate")
    List<PatientProfile> findByBirthDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);
}