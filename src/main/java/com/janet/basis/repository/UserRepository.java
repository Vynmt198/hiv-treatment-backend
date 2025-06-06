package com.janet.basis.repository;

import com.janet.basis.entity.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Account, Long> {

  Optional<Account> findByUsername(String username); // Cần cho Spring Security

  Optional<Account> findByEmail(String email); // Cần để kiểm tra khi đăng ký

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
