package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.entitty.VerificationToken;
import com.hivmedical.medical.repository.UserRepositoty;
import com.hivmedical.medical.repository.VerificationTokenRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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
    Optional<VerificationToken> tokenOpt = tokenRepository.findByEmailAndType(email, "EMAIL_VERIFICATION");
    if (tokenOpt.isPresent() && !tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now()) && tokenOpt.get().getToken().equals(otp)) {
      tokenRepository.delete(tokenOpt.get());
      return true;
    }
    return false;
  }

  public UserEntity getUserByEmail(String email) {
    return userRepositoty.findByEmail(email).orElse(null);
  }

  public void sendPasswordResetOtp(String email, String newPassword) {
    String otp = generateOtp();
    VerificationToken token = new VerificationToken();
    token.setEmail(email);
    token.setToken(otp);
    token.setUserInfo(passwordEncoder.encode(newPassword)); // Lưu mật khẩu đã mã hóa
    token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
    token.setType("PASSWORD_RESET_OTP");
    tokenRepository.save(token);
    emailService.sendOtpEmail(email, otp);
  }

  public boolean verifyOtpAndResetPassword(String email, String otp) {
    Optional<VerificationToken> tokenOpt = tokenRepository.findByEmailAndType(email, "PASSWORD_RESET_OTP");
    if (tokenOpt.isPresent() && !tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now()) && tokenOpt.get().getToken().equals(otp)) {
      UserEntity user = userRepositoty.findByEmail(email).orElse(null);
      if (user != null) {
        user.setPasswordHash(tokenOpt.get().getUserInfo());
        userRepositoty.save(user);
        tokenRepository.delete(tokenOpt.get());
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
