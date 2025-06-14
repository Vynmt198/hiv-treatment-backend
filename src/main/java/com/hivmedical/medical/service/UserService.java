package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.entitty.VerificationToken;
import com.hivmedical.medical.repository.UserRepositoty;
import com.hivmedical.medical.repository.VerificationTokenRepository;
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

  public boolean isEmailExists(String email) {
    return userRepositoty.existsByEmail(email);
  }
  public void registerUserWithOtp(UserEntity user) {
    String otp = generateOtp();
    VerificationToken token = new VerificationToken();
    token.setEmail(user.getEmail());
    token.setToken(otp);
    token.setUserInfo(""); // Có thể lưu thông tin user JSON
    token.setExpiryDate(LocalDateTime.now().plusMinutes(10)); // Hết hạn sau 10 phút
    token.setType("EMAIL_VERIFICATION");
    tokenRepository.save(token);
    // TODO: Gửi OTP qua email (cần tích hợp email service)
  }

  public boolean verifyOtpAndRegister(String email, String otp) {
    Optional<VerificationToken> tokenOpt = tokenRepository.findByEmailAndType(email, "EMAIL_VERIFICATION");
    if (tokenOpt.isPresent() && !tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now()) && tokenOpt.get().getToken().equals(otp)) {
      UserEntity user = new UserEntity();
      user.setUsername(email);
      user.setEmail(email);
      user.setPasswordHash(passwordEncoder.encode("123456")); // Mật khẩu mặc định, cần thay đổi
      user.setEnabled(true);
      userRepositoty.save(user);
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
    token.setUserInfo(""); // Có thể lưu newPassword hash
    token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
    token.setType("PASSWORD_RESET_OTP");
    tokenRepository.save(token);
    // TODO: Gửi OTP qua email
  }

  public boolean verifyOtpAndResetPassword(String email, String otp) {
    Optional<VerificationToken> tokenOpt = tokenRepository.findByEmailAndType(email, "PASSWORD_RESET_OTP");
    if (tokenOpt.isPresent() && !tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now()) && tokenOpt.get().getToken().equals(otp)) {
      UserEntity user = userRepositoty.findByEmail(email).orElse(null);
      if (user != null) {
        user.setPasswordHash(passwordEncoder.encode("newpassword")); // Cập nhật mật khẩu
        userRepositoty.save(user);
        tokenRepository.delete(tokenOpt.get());
        return true;
      }
    }
    return false;
  }

  private String generateOtp() {
    Random random = new Random();
    int otp = 1000 + random.nextInt(9000); // OTP 4 số
    return String.valueOf(otp);
  }
}
