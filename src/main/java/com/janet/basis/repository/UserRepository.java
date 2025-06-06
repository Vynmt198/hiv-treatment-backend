package com.janet.basis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.janet.basis.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsernameAndPassword(String Username, String Password);
}
