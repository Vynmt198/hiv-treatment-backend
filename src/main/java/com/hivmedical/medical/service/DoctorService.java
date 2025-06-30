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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
  private final DoctorRepository doctorRepository;
  private final ScheduleRepository scheduleRepository;
  private final ObjectMapper objectMapper;

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
        .orElseThrow(() -> new RuntimeException("Doctor not found"));
    return convertToDTO(doctor);
  }

  public List<ScheduleDTO> getDoctorSchedule(Long doctorId, String date) {
    LocalDate localDate = LocalDate.parse(date);
    List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDate(doctorId, localDate);
    return schedules.stream().map(this::convertToScheduleDTO).collect(Collectors.toList());
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
}