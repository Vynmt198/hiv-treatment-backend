package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.repository.ScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

  @Autowired
  private ScheduleRepository scheduleRepository;

  public List<Schedule> getAvailableSchedules(Long doctorId, LocalDateTime startTime) {
    return scheduleRepository.findByDoctorIdAndIsAvailableTrueAndStartTimeAfter(doctorId, startTime);
  }

  public void markScheduleAsBooked(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ với ID " + scheduleId + " không tồn tại"));
    schedule.setAvailable(false);
    scheduleRepository.save(schedule);
  }
}