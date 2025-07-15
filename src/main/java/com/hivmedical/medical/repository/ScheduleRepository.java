package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.entitty.Schedule.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  List<Schedule> findByDoctorIdAndDate(Long doctorId, LocalDate date);

  List<Schedule> findByDoctorIdAndIsAvailableTrueAndStartTimeAfter(Long doctorId, LocalDateTime startTime);

  List<Schedule> findByDoctorIdAndDateAndIsAvailableTrue(Long doctorId, LocalDate date);

  List<Schedule> findByDateAndStartTimeAndEndTimeAndIsAvailableTrue(LocalDate date, java.time.LocalDateTime startTime,
      java.time.LocalDateTime endTime);

  List<Schedule> findByStatus(Status status);

  List<Schedule> findByStatusAndPendingUntilBefore(Status status, LocalDateTime time);

  List<Schedule> findByDoctorIdAndDateAndIsAvailableTrueAndStatus(Long doctorId, LocalDate date, Status status);
}
