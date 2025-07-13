package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.ARVProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ARVProtocolRepository extends JpaRepository<ARVProtocol, Long> {

    List<ARVProtocol> findByIsActiveTrue();

    List<ARVProtocol> findByTargetGroup(String targetGroup);

    List<ARVProtocol> findByTargetGroupAndIsActiveTrue(String targetGroup);

    Optional<ARVProtocol> findByNameAndIsActiveTrue(String name);

    @Query("SELECT DISTINCT p.targetGroup FROM ARVProtocol p WHERE p.isActive = true")
    List<String> findAllActiveTargetGroups();

    @Query("SELECT p FROM ARVProtocol p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword% AND p.isActive = true")
    List<ARVProtocol> searchByKeyword(@Param("keyword") String keyword);
}