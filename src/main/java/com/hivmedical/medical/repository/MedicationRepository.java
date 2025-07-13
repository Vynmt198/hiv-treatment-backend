package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findByIsActiveTrue();

    List<Medication> findByDrugClass(String drugClass);

    List<Medication> findByDrugClassAndIsActiveTrue(String drugClass);

    Optional<Medication> findByNameAndIsActiveTrue(String name);

    Optional<Medication> findByGenericNameAndIsActiveTrue(String genericName);

    @Query("SELECT DISTINCT m.drugClass FROM Medication m WHERE m.isActive = true")
    List<String> findAllActiveDrugClasses();

    @Query("SELECT m FROM Medication m WHERE m.name LIKE %:keyword% OR m.genericName LIKE %:keyword% OR m.brandName LIKE %:keyword% AND m.isActive = true")
    List<Medication> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT m FROM Medication m WHERE m.drugClass = :drugClass AND m.isActive = true ORDER BY m.name")
    List<Medication> findActiveByDrugClass(@Param("drugClass") String drugClass);
}