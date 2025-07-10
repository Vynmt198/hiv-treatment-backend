package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.entitty.VerificationToken;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.Role;
import com.hivmedical.medical.repository.UserRepositoty;
import com.hivmedical.medical.repository.VerificationTokenRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.hivmedical.medical.dto.DoctorDTO;
import com.hivmedical.medical.entitty.Doctor;
import com.hivmedical.medical.entitty.Role;
import java.util.HashMap;
import java.util.Map;
import com.hivmedical.medical.repository.DoctorRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

  @Autowired
  private UserRepositoty userRepository;

  @Autowired
  private VerificationTokenRepository tokenRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private EmailService emailService;

  // [NODE] Thêm khai báo biến tĩnh PASSWORD_CHARACTERS để tránh lỗi trong generateRandomPassword
  private static final String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  public boolean isEmailExists(String email) {
    return userRepository.existsByEmail(email);
  }

  public void registerUserWithOtp(UserEntity user) {
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("Email không được để trống");
    }
    String otp = generateOtp();
    VerificationToken token = new VerificationToken();
    token.setEmail(user.getEmail());
    token.setToken(otp);
    token.setUserInfo("");
    token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
    token.setType("EMAIL_VERIFICATION");
    tokenRepository.save(token);
    emailService.sendOtpEmail(user.getEmail(), otp);
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
    tokenRepository.delete(token);
    return true;
  }

  public UserEntity getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }

  public void sendPasswordResetOtp(String email, String newPassword) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email không được để trống");
    }
    if (newPassword == null || newPassword.trim().isEmpty()) {
      throw new IllegalArgumentException("Mật khẩu mới không được để trống");
    }
    if (!userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Email không tồn tại trong hệ thống");
    }
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
    UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
    user.setPasswordHash(token.getUserInfo());
    userRepository.save(user);
    tokenRepository.delete(token);
    return true;
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

  // [NODE] Thêm import cho DoctorRepository


  // [NODE] Sửa lỗi: Thêm @Autowired cho doctorRepository
  @Autowired
  private DoctorRepository doctorRepository;

  // [NODE] Thêm phương thức createOfficialDoctorAccount để tạo tài khoản bác sĩ chính thức ngay lập tức
  public Map<String, Object> createOfficialDoctorAccount(DoctorDTO dto) {
    logger.info("Starting creation of doctor account for email: {}", dto.getEmail());
    String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUsername));
    if (!currentUser.getRole().equals(Role.ADMIN)) {
      throw new IllegalArgumentException("Only admins can create doctor accounts");
    }

    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
    }

    String rawPassword = generateRandomPassword();
    String encodedPassword = passwordEncoder.encode(rawPassword);

    UserEntity user = new UserEntity();
    user.setEmail(dto.getEmail());
    user.setFullName(dto.getFullName());
    user.setPasswordHash(encodedPassword);
    user.setRegistrationDate(LocalDateTime.now());
    user.setRole(Role.DOCTOR);
    user.setUsername(dto.getEmail().split("@")[0]);
    user.setEnabled(true);

    UserEntity savedUser;
    try {
      savedUser = userRepository.save(user);
      logger.info("User saved successfully with userId: {}", savedUser.getUserId());
    } catch (Exception e) {
      logger.error("Failed to save user: {}", e.getMessage());
      throw new IllegalArgumentException("Failed to save user: " + e.getMessage());
    }

    // [NODE] Sử dụng mối quan hệ với UserEntity thay vì gán id thủ công
    Doctor doctor = new Doctor();
    doctor.setEmail(dto.getEmail());
    doctor.setFullName(dto.getFullName());
    doctor.setPhoneNumber(dto.getPhoneNumber());
    doctor.setSpecialization(dto.getSpecialization());
    doctor.setQualification(dto.getQualification());
    doctor.setWorkingSchedule(dto.getWorkingSchedule());
    doctor.setImageUrl(dto.getImageUrl());
    doctor.setCreatedAt(LocalDateTime.now());
    doctor.setUpdatedAt(LocalDateTime.now());
    doctor.setUser(savedUser); // Liên kết với UserEntity

    try {
      doctorRepository.save(doctor);
      logger.info("Doctor saved successfully with id: {}", doctor.getId());
    } catch (Exception e) {
      logger.error("Failed to save doctor: {}, Stack trace: {}", e.getMessage(), e.getStackTrace());
      userRepository.delete(savedUser);
      throw new IllegalArgumentException("Failed to save doctor: " + e.getMessage());
    }

    sendPasswordEmail(dto.getEmail(), rawPassword);

    Map<String, Object> response = new HashMap<>();
    response.put("userId", savedUser.getUserId());
    response.put("email", savedUser.getEmail());
    response.put("username", savedUser.getUsername());
    response.put("message", "Doctor account created successfully and activated. Password has been sent to the doctor's email.");
    return response;
  }

  // [NODE] Thêm phương thức sendPasswordEmail để gửi email chứa mật khẩu
  private void sendPasswordEmail(String toEmail, String password) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject("Your Doctor Account Credentials");
    message.setText("Welcome! Your account has been created and activated.\n\nUsername: " + toEmail.split("@")[0] + "\nPassword: " + password + "\n\nPlease change your password after first login.");
    message.setFrom("vynmtse181954@fpt.edu.vn"); // Thay bằng email cấu hình
    try {
      emailService.sendOtpEmail(toEmail,password);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to send password email: " + e.getMessage());
    }
  }

  // [NODE] Thêm phương thức generateRandomPassword để tạo mật khẩu ngẫu nhiên
  private String generateRandomPassword() {
    SecureRandom random = new SecureRandom();
    StringBuilder password = new StringBuilder(12);
    for (int i = 0; i < 12; i++) {
      password.append(PASSWORD_CHARACTERS.charAt(random.nextInt(PASSWORD_CHARACTERS.length())));
    }
    return password.toString();
  }
}