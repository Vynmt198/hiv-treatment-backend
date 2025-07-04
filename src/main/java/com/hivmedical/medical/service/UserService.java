package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.entitty.VerificationToken;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.entitty.VerificationToken;
import com.hivmedical.medical.repository.UserRepositoty;
import com.hivmedical.medical.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.security.SecureRandom;

@Service
public class UserService {

  @Autowired
  private UserRepositoty userRepository; // Sửa chính tả từ userRepositoty

  @Autowired
  private VerificationTokenRepository tokenRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private EmailService emailService;

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
}