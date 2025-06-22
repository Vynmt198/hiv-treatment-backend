package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.entitty.VerificationToken;
import com.hivmedical.medical.repository.UserRepositoty;
import com.hivmedical.medical.repository.VerificationTokenRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserRepositoty userRepositoty;

  @Autowired
  private VerificationTokenRepository tokenRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;
  @Autowired
  private EmailService emailService;

  public boolean isEmailExists(String email) {
    return userRepositoty.existsByEmail(email);
  }

  public void registerUserWithOtp(UserEntity user) {
    String otp = generateOtp();
    logger.info("Generating OTP {} for email: {}", otp, user.getEmail());
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
    logger.info("Verifying OTP for email: {}", email);
    List<VerificationToken> tokens = tokenRepository.findByEmailAndType(email, "EMAIL_VERIFICATION");
    if (tokens.isEmpty()) {
      logger.warn("No verification token found for email: {}", email);
      return false;
    }
    VerificationToken latestToken = tokens.stream()
        .filter(t -> !t.getExpiryDate().isBefore(LocalDateTime.now()))
        .max((t1, t2) -> t1.getExpiryDate().compareTo(t2.getExpiryDate()))
        .orElse(null);
    if (latestToken != null && latestToken.getToken().equals(otp)) {
      tokenRepository.delete(latestToken);
      return true;
    }
    logger.warn("OTP verification failed for email: {} - OTP invalid or expired", email);
    return false;
  }

  public UserEntity getUserByEmail(String email) {
    return userRepositoty.findByEmail(email).orElse(null);
  }

  public void sendPasswordResetOtp(String email) {
    String otp = generateOtp();
    logger.info("Generating password reset OTP {} for email: {}", otp, email);
    VerificationToken token = new VerificationToken();
    token.setEmail(email);
    token.setToken(otp);
    token.setUserInfo(""); // Không lưu mật khẩu mới ở đây
    token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
    token.setType("PASSWORD_RESET_OTP");
    tokenRepository.save(token);
    emailService.sendOtpEmail(email, otp);
  }

  public boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) {
    logger.info("Verifying password reset OTP for email: {}", email);
    List<VerificationToken> tokens = tokenRepository.findByEmailAndType(email, "PASSWORD_RESET_OTP");
    if (tokens.isEmpty()) {
      logger.warn("No password reset token found for email: {}", email);
      return false;
    }
    VerificationToken latestToken = tokens.stream()
        .filter(t -> !t.getExpiryDate().isBefore(LocalDateTime.now()))
        .max((t1, t2) -> t1.getExpiryDate().compareTo(t2.getExpiryDate()))
        .orElse(null);
    if (latestToken != null && latestToken.getToken().equals(otp)) {
      UserEntity user = userRepositoty.findByEmail(email).orElse(null);
      if (user != null) {
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepositoty.save(user);
        tokenRepository.delete(latestToken);
        return true;
      }
    }
    return false;
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