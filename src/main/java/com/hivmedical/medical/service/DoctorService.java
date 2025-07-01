package com.hivmedical.medical.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivmedical.medical.dto.DoctorDTO;
import com.hivmedical.medical.dto.ScheduleDTO;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.repository.DoctorRepository;
import com.hivmedical.medical.repository.ScheduleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
  private final DoctorRepository doctorRepository;
  private final ScheduleRepository scheduleRepository;
  private final ObjectMapper objectMapper;

  private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
  public List<Doctor> getAllDoctors() {
    return doctorRepository.findAll();
  }

  public DoctorService(DoctorRepository doctorRepository, ScheduleRepository scheduleRepository,
      ObjectMapper objectMapper) {
    this.doctorRepository = doctorRepository;
    this.scheduleRepository = scheduleRepository;
    this.objectMapper = objectMapper;
  }

  public Page<DoctorDTO> getDoctors(String search, String searchBy, Pageable pageable) {
    Page<Doctor> doctors;
    if (search != null && !search.isEmpty()) {
      if ("name".equalsIgnoreCase(searchBy)) {
        doctors = doctorRepository.findByNameContainingIgnoreCase(search, pageable);
      } else {
        doctors = doctorRepository.findBySpecializationContainingIgnoreCase(search, pageable);
      }
    } else {
      doctors = doctorRepository.findAll(pageable);
    }
    return doctors.map(this::convertToDTO);
  }

  public DoctorDTO getDoctorById(Long id) {
    Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Bác sĩ với ID " + id + " không tồn tại"));
    return convertToDTO(doctor);
  }

  private DoctorDTO convertToDTO(Doctor doctor) {
    DoctorDTO dto = new DoctorDTO();
    dto.setId(doctor.getId());
    dto.setFullName(doctor.getFullName());
    dto.setSpecialization(doctor.getSpecialization());
    dto.setQualification(doctor.getQualification());
    dto.setEmail(doctor.getEmail());
    dto.setPhoneNumber(doctor.getPhoneNumber());
    dto.setWorkingSchedule(doctor.getWorkingSchedule());
    dto.setImageUrl(doctor.getImageUrl());
    return dto;
  }

  private ScheduleDTO convertToScheduleDTO(Schedule schedule) {
    ScheduleDTO dto = new ScheduleDTO();
    dto.setId(schedule.getId());
    dto.setDoctorId(schedule.getDoctor().getId());
    dto.setDate(schedule.getDate());
    try {
      List<String> timeSlots = objectMapper.readValue(schedule.getTimeSlots(), new TypeReference<List<String>>() {});
      dto.setTimeSlots(timeSlots);
    } catch (Exception e) {
      throw new RuntimeException("Error parsing time slots", e);
    }
    return dto;
  }

  public List<ScheduleDTO> getDoctorSchedule(Long id, String date) {
    logger.debug("Fetching schedules for doctorId: {}, date: {}", id, date);
    LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
    List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDate(id, localDate);
    logger.debug("Found {} schedules in database for doctorId: {}", schedules.size(), id);
    return schedules.stream()
        .filter(schedule -> {
          if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            logger.warn("Schedule {} has null startTime or endTime", schedule.getId());
            return false;
          }
          return schedule.isAvailable();
        })
        .map(schedule -> {
          ScheduleDTO dto = new ScheduleDTO();
          dto.setId(schedule.getId());
          dto.setDoctorId(id);
          dto.setDate(schedule.getDate());
          dto.setTimeSlots(null); // Can be parsed if needed
          dto.setStartTime(schedule.getStartTime());
          dto.setEndTime(schedule.getEndTime());
          dto.setAvailable(schedule.isAvailable());
          dto.setCreatedAt(schedule.getCreatedAt());
          dto.setUpdatedAt(schedule.getUpdatedAt());
          return dto;
        })
        .collect(Collectors.toList());
  }
}