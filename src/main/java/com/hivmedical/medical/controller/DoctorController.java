package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.DoctorDTO;
import com.hivmedical.medical.dto.ScheduleDTO;
import com.hivmedical.medical.service.DoctorService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
  private final DoctorService doctorService;

  public DoctorController(DoctorService doctorService) {
    this.doctorService = doctorService;
  }

  @GetMapping
  public ResponseEntity<Page<DoctorDTO>> getDoctors(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "name") String searchBy) {
    Pageable pageable = PageRequest.of(page, size);
    Page<DoctorDTO> doctors = doctorService.getDoctors(search, searchBy, pageable);
    return ResponseEntity.ok(doctors);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
    DoctorDTO doctor = doctorService.getDoctorById(id);
    return ResponseEntity.ok(doctor);
  }

  @GetMapping("/{id}/schedule")
  public ResponseEntity<List<ScheduleDTO>> getDoctorSchedule(
      @PathVariable Long id,
      @RequestParam String date) {
    List<ScheduleDTO> schedule = doctorService.getDoctorSchedule(id, date);
    return ResponseEntity.ok(schedule);
  }


//  @GetMapping("/patients")
//  @PreAuthorize("hasRole('DOCTOR')")
//  public ResponseEntity<String> getPatients() {
//    return ResponseEntity.ok("List of patients for doctor");
//  }
}