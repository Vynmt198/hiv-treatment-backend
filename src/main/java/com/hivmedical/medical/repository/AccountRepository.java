package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Account;
import com.hivmedical.medical.entitty.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Account> findByRole(Role role);

    List<Account> findByEnabled(boolean enabled);

    @Query("SELECT a FROM Account a WHERE a.role = :role AND a.enabled = true")
    List<Account> findActiveAccountsByRole(@Param("role") Role role);

    @Query("SELECT a FROM Account a WHERE a.username LIKE %:keyword% OR a.email LIKE %:keyword% OR a.role = :role")
    List<Account> findByKeywordOrRole(@Param("keyword") String keyword, @Param("role") Role role);
}