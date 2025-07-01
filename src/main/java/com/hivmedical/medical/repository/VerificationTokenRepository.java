package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.VerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  Optional<VerificationToken> findByToken(String token);
  Optional<VerificationToken> findByEmailAndType(String email, String type);
  void deleteByEmailAndType(String email, String type);
}