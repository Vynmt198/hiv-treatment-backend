package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.VerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  Optional<VerificationToken> findByToken(String token);

  Optional<VerificationToken> findByEmailAndType(String email, String type);

  @Transactional
  void deleteByEmailAndType(String email, String type);
}