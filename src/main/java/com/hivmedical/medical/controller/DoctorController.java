package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.DoctorDTO;
import com.hivmedical.medical.dto.ScheduleDTO;
import com.hivmedical.medical.service.DoctorService;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // Read (danh sách với tìm kiếm và phân trang)
    @GetMapping
    public ResponseEntity<Page<DoctorDTO>> getDoctors(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "searchBy", required = false) String searchBy,
            Pageable pageable) {
        return ResponseEntity.ok(doctorService.getDoctors(search, searchBy, pageable));
    }

    // Update
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long id, @RequestBody DoctorDTO dto) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, dto));
    }

    // Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        DoctorDTO doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<DoctorDTO> getDoctorByAccountId(@PathVariable Long accountId) {
        DoctorDTO doctor = doctorService.getDoctorByAccountId(accountId);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/{id}/schedule")
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

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleDTO>> getDoctorSchedules(
            @PathVariable Long id,
            @RequestParam(required = false) String date) {
        logger.debug("Fetching schedules for doctorId: {}, date: {}", id, date);
        try {
            List<ScheduleDTO> schedules = doctorService.getDoctorSchedule(id, date);
            logger.debug("Retrieved {} schedules for doctorId: {}", schedules.size(), id);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            logger.error("Error fetching schedules for doctorId: {} and date: {}", id, date, e);
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
            logger.error(
                    "Error fetching available doctors for date: {}, startTime: {}, endTime: {}, specialization: {}",
                    date, startTime, endTime, specialization, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}