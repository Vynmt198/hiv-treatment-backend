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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

  private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
  private final DoctorService doctorService;

  public DoctorController(DoctorService doctorService) {
    this.doctorService = doctorService;
  }


  @PostMapping("/create")
  public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorDTO dto) {
    return ResponseEntity.ok(doctorService.createDoctor(dto));
  }

  // Read (danh sách với tìm kiếm và phân trang)
  @GetMapping
  public ResponseEntity<Page<DoctorDTO>> getDoctors(
          @RequestParam(required = false) String search,
          @RequestParam(required = false) String searchBy,
          Pageable pageable) {
    return ResponseEntity.ok(doctorService.getDoctors(search, searchBy, pageable));
  }
  // Update
  @PutMapping("/{id}")
  public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long id, @RequestBody DoctorDTO dto) {
    return ResponseEntity.ok(doctorService.updateDoctor(id, dto));
  }
  // Delete
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
    doctorService.deleteDoctor(id);
    return ResponseEntity.noContent().build();
  }


  @GetMapping("/{id}")
  public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
    DoctorDTO doctor = doctorService.getDoctorById(id);
    return ResponseEntity.ok(doctor);
  }

  @GetMapping("/{id}/schedules")
  public ResponseEntity<List<ScheduleDTO>> getDoctorAvailableSchedules(
          @PathVariable Long id,
          @RequestParam(required = false) String date) {
    logger.debug("Fetching available schedules for doctorId: {}, date: {}", id, date);
    try {
      LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
      List<ScheduleDTO> schedules = doctorService.getAvailableDoctorSchedules(id, localDate.toString());
      logger.debug("Retrieved {} available schedules for doctorId: {}", schedules.size(), id);
      return ResponseEntity.ok(schedules);
    } catch (Exception e) {
      logger.error("Error fetching available schedules for doctorId: {} and date: {}", id, date, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @GetMapping("/available")
  public ResponseEntity<List<DoctorDTO>> getAvailableDoctorsBySlot(
      @RequestParam String date,
      @RequestParam String startTime,
      @RequestParam String endTime,
      @RequestParam(required = false) String specialization) {
    try {
      List<DoctorDTO> availableDoctors = doctorService.getAvailableDoctorsBySlot(date, startTime, endTime,
          specialization);
      return ResponseEntity.ok(availableDoctors);
    } catch (Exception e) {
      logger.error("Error fetching available doctors for date: {}, startTime: {}, endTime: {}, specialization: {}",
          date, startTime, endTime, specialization, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}