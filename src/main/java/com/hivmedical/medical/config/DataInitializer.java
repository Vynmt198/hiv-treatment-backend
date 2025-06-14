package com.hivmedical.medical.config;

import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.repository.UserRepositoty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {


@Autowired
  private UserRepositoty userRepositoty;

@Override
public void run(String... args) {
  if (userRepositoty.findByUsername("test").isEmpty()) {
    UserEntity testUser = new UserEntity();
    testUser.setUsername("test");
    testUser.setPasswordHash("123456"); // Lưu plain text tạm thời
    userRepositoty.save(testUser);
  }
}
}
