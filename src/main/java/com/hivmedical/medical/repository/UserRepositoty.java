package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoty extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
  Optional<UserEntity> findByEmail(String email);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);


}
