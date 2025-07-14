package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.Schedule;
import com.hivmedical.medical.repository.ScheduleRepository;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hivmedical.medical.entitty.Schedule.Status;
import org.springframework.scheduling.annotation.Scheduled;

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

  public List<Schedule> getAvailableSchedules(Long doctorId, LocalDate date) {
    return scheduleRepository.findByDoctorIdAndDateAndIsAvailableTrue(doctorId, date);
  }

  // Đặt lịch: chuyển slot sang PENDING, giữ chỗ 5 phút
  public void holdScheduleForBooking(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ với ID " + scheduleId + " không tồn tại"));
    if (schedule.getStatus() != Status.AVAILABLE) {
      throw new IllegalStateException("Slot này không khả dụng");
    }
    schedule.setStatus(Status.PENDING);
    schedule.setPendingUntil(LocalDateTime.now().plusMinutes(5));
    scheduleRepository.save(schedule);
  }

  // Xác nhận thanh toán thành công: chuyển slot sang BOOKED
  public void confirmScheduleBooking(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Khung giờ với ID " + scheduleId + " không tồn tại"));
    if (schedule.getStatus() != Status.PENDING) {
      throw new IllegalStateException("Slot này không ở trạng thái chờ thanh toán");
    }
    schedule.setStatus(Status.BOOKED);
    schedule.setPendingUntil(null);
    scheduleRepository.save(schedule);
  }

  // Scheduled job: giải phóng slot PENDING hết hạn giữ chỗ
  @Scheduled(fixedRate = 60000) // mỗi phút
  public void releaseExpiredPendingSchedules() {
    List<Schedule> expired = scheduleRepository.findByStatusAndPendingUntilBefore(Status.PENDING, LocalDateTime.now());
    for (Schedule s : expired) {
      s.setStatus(Status.AVAILABLE);
      s.setPendingUntil(null);
      scheduleRepository.save(s);
    }
  }
}