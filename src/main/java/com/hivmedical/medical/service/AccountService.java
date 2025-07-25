package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.PatientProfileDTO;
import com.hivmedical.medical.entitty.*;
import com.hivmedical.medical.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.security.SecureRandom;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.repository.DoctorRepository;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.repository.UserRepositoty;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private AdminProfileRepository adminProfileRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepositoty userRepository;

    @Autowired
    private UserService userService;

    public boolean isEmailExists(String email) {
        return accountRepository.existsByEmail(email);
    }

    public boolean isUsernameExists(String username) {
        return accountRepository.existsByUsername(username);
    }

    public Account createAccount(String username, String email, String password, Role role) {
        if (isEmailExists(email)) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (isUsernameExists(username)) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        Account account = Account.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .registrationDate(LocalDateTime.now())
                .build();

        return accountRepository.save(account);
    }

    public void registerUserWithOtp(String username, String email, String password, Role role) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (isEmailExists(email)) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        String otp = generateOtp();
        VerificationToken token = new VerificationToken();
        token.setEmail(email);
        token.setToken(otp);
        token.setUserInfo(
                "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"role\":\"" + role + "\"}");
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setType("EMAIL_VERIFICATION");
        tokenRepository.save(token);
        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtpAndRegister(String email, String otp) {
        if (email == null || email.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
            throw new IllegalArgumentException("Email và OTP không được để trống");
        }

        Optional<VerificationToken> tokenOpt = tokenRepository.findByEmailAndType(email, "EMAIL_VERIFICATION");
        if (!tokenOpt.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy OTP cho email này");
        }

        VerificationToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new IllegalArgumentException("OTP đã hết hạn");
        }

        if (!token.getToken().equals(otp)) {
            throw new IllegalArgumentException("OTP không đúng");
        }

        // Parse user info from token
        String userInfo = token.getUserInfo();
        // TODO: Parse JSON userInfo to get username, password, role
        // For now, create a simple account
        Account account = createAccount(email, email, "defaultPassword", Role.PATIENT);

        // Create corresponding profile
        createPatientProfile(account, email, "2000-01-01");

        tokenRepository.delete(token);
        return true;
    }

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email).orElse(null);
    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username).orElse(null);
    }

    public void sendPasswordResetOtp(String email, String newPassword) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }
        if (!accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email không tồn tại trong hệ thống");
        }

        // Xóa token OTP cũ
        tokenRepository.deleteByEmailAndType(email, "PASSWORD_RESET_OTP");
        String otp = generateOtp();
        VerificationToken token = new VerificationToken();
        token.setEmail(email);
        token.setToken(otp);
        token.setUserInfo(passwordEncoder.encode(newPassword));
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setType("PASSWORD_RESET_OTP");
        tokenRepository.save(token);
        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtpAndResetPassword(String email, String otp) {
        if (email == null || email.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
            throw new IllegalArgumentException("Email và OTP không được để trống");
        }

        Optional<VerificationToken> tokenOpt = tokenRepository.findByEmailAndType(email, "PASSWORD_RESET_OTP");
        if (!tokenOpt.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy OTP cho email này");
        }

        VerificationToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new IllegalArgumentException("OTP đã hết hạn");
        }

        if (!token.getToken().equals(otp)) {
            throw new IllegalArgumentException("OTP không đúng");
        }

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));
        account.setPasswordHash(token.getUserInfo());
        accountRepository.save(account);
        tokenRepository.delete(token);
        return true;
    }

    public PatientProfileDTO getPatientProfile(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        PatientProfile profile = patientProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        return mapToProfileDTO(profile);
    }

    public PatientProfileDTO updatePatientProfile(String email, PatientProfileDTO dto) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        PatientProfile profile = patientProfileRepository.findByAccount(account)
                .orElseGet(() -> createPatientProfile(account, dto.getFullName(), dto.getBirthDate().toString()));

        profile.setFullName(dto.getFullName());
        profile.setGender(dto.getGender());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setBirthDate(dto.getBirthDate());
        profile.setHivStatus(dto.getHivStatus());
        profile.setTreatmentStartDate(dto.getTreatmentStartDate());

        PatientProfile savedProfile = patientProfileRepository.save(profile);
        return mapToProfileDTO(savedProfile);
    }

    public PatientProfile createPatientProfile(Account account, String fullName, String birthDate) {
        PatientProfile profile = PatientProfile.builder()
                .account(account)
                .fullName(fullName != null ? fullName : account.getUsername())
                .birthDate(LocalDateTime.parse(birthDate + "T00:00:00").toLocalDate())
                .treatmentStartDate(LocalDateTime.now().toLocalDate())
                .build();

        return patientProfileRepository.save(profile);
    }

    public DoctorProfile createDoctorProfile(Account account, String fullName, String specialization) {
        DoctorProfile profile = DoctorProfile.builder()
                .account(account)
                .fullName(fullName != null ? fullName : account.getUsername())
                .specialization(specialization != null ? specialization : "HIV/AIDS")
                .qualification("Bác sĩ")
                .workingSchedule("Thứ 2 - Thứ 6, 08:00-17:00")
                .build();

        return doctorProfileRepository.save(profile);
    }

    public AdminProfile createAdminProfile(Account account, String fullName) {
        AdminProfile profile = AdminProfile.builder()
                .account(account)
                .fullName(fullName != null ? fullName : account.getUsername())
                .department("Quản trị hệ thống")
                .build();

        return adminProfileRepository.save(profile);
    }

    public void updateLastLoginDate(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setLastLoginDate(LocalDateTime.now());
        accountRepository.save(account);
    }

    private String generateOtp() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            otp.append(characters.charAt(random.nextInt(characters.length())));
        }
        return otp.toString();
    }

    private PatientProfileDTO mapToProfileDTO(PatientProfile profile) {
        return new PatientProfileDTO(
                profile.getId(),
                profile.getFullName(),
                profile.getGender(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getBirthDate(),
                profile.getHivStatus(),
                profile.getTreatmentStartDate());
    }

    /**
     * Gán quyền DOCTOR cho account dựa trên doctorId
     * 
     * @param doctorId id của doctor (liên kết với account)
     * @return true nếu thành công, false nếu không tìm thấy doctor/account
     */
    public boolean assignDoctorRoleByDoctorId(Long doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty())
            return false;
        Doctor doctor = doctorOpt.get();
        Account account = doctor.getAccount();
        if (account == null)
            return false;
        if (account.getRole() == Role.DOCTOR)
            return true; // Đã là doctor
        account.setRole(Role.DOCTOR);
        accountRepository.save(account);
        // Tạo DoctorProfile nếu chưa có
        if (doctorProfileRepository.findByAccount(account).isEmpty()) {
            createDoctorProfile(account, doctor.getFullName(), doctor.getSpecialization());
        }
        return true;
    }

    /**
     * Staff thêm mới bệnh nhân: tạo account (nếu chưa có) và profile, đồng bộ thông
     * tin
     */
    public boolean createPatientByStaff(com.hivmedical.medical.dto.PatientRegisterByStaffRequest request) {
        // Kiểm tra account đã tồn tại qua email hoặc username (dùng email làm username
        // nếu chưa có)
        Optional<Account> existingAccount = accountRepository.findByEmail(request.getEmail());
        Account account;
        if (existingAccount.isPresent()) {
            account = existingAccount.get();
            // Nếu đã có profile bệnh nhân, không cho phép tạo trùng
            if (patientProfileRepository.findByAccount(account).isPresent()) {
                throw new IllegalArgumentException("Bệnh nhân đã tồn tại với email này!");
            }
            // Không gửi lại mật khẩu nếu account đã tồn tại
        } else {
            // Tạo account mới
            String username = request.getEmail();
            String password = generateRandomPassword();
            account = Account.builder()
                    .username(username)
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(password))
                    .role(Role.PATIENT)
                    .enabled(true)
                    .registrationDate(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            accountRepository.save(account);
            // Gửi email thông báo tài khoản cho bệnh nhân
            emailService.sendAccountInfo(request.getEmail(), request.getFullName(), username, password);
        }
        // Sau khi accountRepository.save(account);
        UserEntity user = new UserEntity();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(account.getPasswordHash());
        user.setUsername(request.getEmail());
        user.setRole(Role.PATIENT);
        user.setEnabled(true);
        userService.save(user); // hoặc userRepository.save(user);
        // Tạo profile bệnh nhân
        PatientProfile profile = PatientProfile.builder()
                .account(account)
                .fullName(request.getFullName())
                .gender(request.getGender())
                .phone(request.getPhoneNumber())
                .address(request.getAddress())
                .birthDate(java.time.LocalDate.parse(request.getDateOfBirth()))
                .treatmentStartDate(java.time.LocalDate.now()) // hoặc cho phép nhập nếu cần
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        patientProfileRepository.save(profile);
        return true;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}