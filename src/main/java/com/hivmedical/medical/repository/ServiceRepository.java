package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.ServiceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
  List<ServiceEntity> findByType(String type);

  Optional<ServiceEntity> findByName(String name);
}