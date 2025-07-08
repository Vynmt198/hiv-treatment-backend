package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Account;
import com.hivmedical.medical.entitty.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {

    Optional<AdminProfile> findByAccount(Account account);

    Optional<AdminProfile> findByAccountId(Long accountId);

    Optional<AdminProfile> findByAccountUsername(String username);

    Optional<AdminProfile> findByAccountEmail(String email);

    @Query("SELECT ap FROM AdminProfile ap WHERE ap.fullName LIKE %:keyword% OR ap.department LIKE %:keyword%")
    List<AdminProfile> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT ap FROM AdminProfile ap WHERE ap.department = :department")
    List<AdminProfile> findByDepartment(@Param("department") String department);
}