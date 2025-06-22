package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.VerificationToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  List<VerificationToken> findByEmailAndType(String email, String type);
  Optional<VerificationToken> findByToken(String token);
}