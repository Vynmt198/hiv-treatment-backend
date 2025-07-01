package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.DoctorDTO;
import com.hivmedical.medical.dto.ScheduleDTO;
import com.hivmedical.medical.service.DoctorService;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

  private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
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
      @RequestParam(required = false) String date) {
    logger.debug("Fetching schedules for doctorId: {}, date: {}", id, date);
    try {
      LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
      List<ScheduleDTO> schedules = doctorService.getDoctorSchedule(id, localDate.toString());
      logger.debug("Retrieved {} schedules for doctorId: {}", schedules.size(), id);
      return ResponseEntity.ok(schedules);
    } catch (Exception e) {
      logger.error("Error fetching schedules for doctorId: {} and date: {}", id, date, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null); // Temporary fallback
    }
  }
}