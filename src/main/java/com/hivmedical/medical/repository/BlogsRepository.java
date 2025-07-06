package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Blogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogsRepository extends JpaRepository<Blogs, Long> {
}