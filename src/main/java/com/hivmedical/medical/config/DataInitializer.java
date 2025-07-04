package com.hivmedical.medical.config;

import com.hivmedical.medical.entitty.Role;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.repository.UserRepositoty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private UserRepositoty userRepositoty;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (userRepositoty.findByUsername("admin").isEmpty()) {
      UserEntity admin = new UserEntity();
      admin.setUsername("admin");
      admin.setPasswordHash(passwordEncoder.encode("Admin123"));
      admin.setEmail("admin@example.com");
      admin.setRole(Role.ADMIN);
      admin.setEnabled(true);
      userRepositoty.save(admin);
    }
    if (userRepositoty.findByUsername("doctor").isEmpty()) {
      UserEntity doctor = new UserEntity();
      doctor.setUsername("doctor");
      doctor.setPasswordHash(passwordEncoder.encode("Doctor123"));
      doctor.setEmail("doctor@example.com");
      doctor.setRole(Role.DOCTOR);
      doctor.setEnabled(true);
      userRepositoty.save(doctor);
    }
    if (userRepositoty.findByUsername("staff").isEmpty()) {
      UserEntity staff = new UserEntity();
      staff.setUsername("staff"); 
      staff.setPasswordHash(passwordEncoder.encode("Staff123"));
      staff.setEmail("staff@example.com");
      staff.setRole(Role.STAFF);
      staff.setEnabled(true);
      userRepositoty.save(staff);
    }
    if (userRepositoty.findByUsername("patient").isEmpty()) {
      UserEntity patient = new UserEntity();
      patient.setUsername("patient");
      patient.setPasswordHash(passwordEncoder.encode("Patient123"));
      patient.setEmail("patient@example.com");
      patient.setRole(Role.PATIENT);
      patient.setEnabled(true);
      userRepositoty.save(patient);
    }
  }
}