package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.OverviewDTO;
import com.hivmedical.medical.repository.AccountRepository;
import com.hivmedical.medical.repository.DoctorRepository;
import com.hivmedical.medical.repository.AdminProfileRepository;
import com.hivmedical.medical.repository.PatientProfileRepository;
import com.hivmedical.medical.repository.AppointmentRepository;
import com.hivmedical.medical.entitty.AppointmentStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AccountRepository accountRepository;
    private final DoctorRepository doctorRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminService(
        AccountRepository accountRepository,
        DoctorRepository doctorRepository,
        AdminProfileRepository adminProfileRepository,
        PatientProfileRepository patientProfileRepository,
        AppointmentRepository appointmentRepository
    ) {
        this.accountRepository = accountRepository;
        this.doctorRepository = doctorRepository;
        this.adminProfileRepository = adminProfileRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public OverviewDTO getOverview() {
        long totalUsers = accountRepository.count();
        long activeDoctors = doctorRepository.count();
        long activeStaff = adminProfileRepository.count();
        long newPatientsThisMonth = 0; // TODO: Bổ sung logic nếu cần
        double userGrowthRate = 0.0;   // TODO: Bổ sung logic nếu cần

        return new OverviewDTO(totalUsers, newPatientsThisMonth, activeDoctors, activeStaff, userGrowthRate);
    }

    public long getTotalPatients() {
        return patientProfileRepository.count();
    }

    public long getTotalAppointments() {
        return appointmentRepository.count();
    }

    public long getPendingAppointments() {
        return appointmentRepository.countByStatus(AppointmentStatus.PENDING);
    }

    public long getTotalDoctors() {
        return doctorRepository.count();
    }
}
