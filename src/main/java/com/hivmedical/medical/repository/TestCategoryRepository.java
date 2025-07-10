package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.TestCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCategoryRepository extends JpaRepository<TestCategory, Long> {
    TestCategory findByName(String name);
}