package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.ForgotPasswordOtpRequest;
import com.hivmedical.medical.dto.LoginRequest;
import com.hivmedical.medical.dto.RegisterRequest;
import com.hivmedical.medical.dto.VerifyOtpRequest;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthAPI {

  @Autowired
  private UserService userService;

  /**
   * BƯỚC 1: Đăng ký - Gửi OTP về email
   * Endpoint: POST /api/auth/register-request
   */
  @PostMapping("/register-request")
  public ResponseEntity<Map<String, Object>> registerRequest(@RequestBody RegisterRequest request) {
    Map<String, Object> response = new HashMap<>();
    try {
      if (userService.isEmailExists(request.getEmail())) {
        response.put("success", false);
        response.put("message", "Email đã được sử dụng");
        return ResponseEntity.badRequest().body(response);
      }
      if (!request.getPassword().equals(request.getConfirmPassword())) {
        response.put("success", false);
        response.put("message", "Mật khẩu xác nhận không khớp");
        return ResponseEntity.badRequest().body(response);
      }
      UserEntity user = new UserEntity();
      user.setFullName(request.getFullName());
      user.setEmail(request.getEmail());
      user.setPasswordHash(request.getPassword()); // Plain text tạm thời
      user.setUsername(request.getEmail());
      user.setRole("member");
      userService.registerUserWithOtp(user);

      response.put("success", true);
      response.put("message", "OTP đã được gửi về email. Vui lòng kiểm tra email để xác thực!");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Đăng ký thất bại: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * BƯỚC 2: Xác nhận OTP để hoàn tất đăng ký
   * Endpoint: POST /api/auth/register-verify-otp
   */
  @PostMapping("/register-verify-otp")
  public ResponseEntity<Map<String, Object>> verifyRegisterOtp(@RequestBody VerifyOtpRequest request) {
    Map<String, Object> response = new HashMap<>();
    boolean ok = userService.verifyOtpAndRegister(request.getEmail(), request.getOtp());
    if (ok) {
      response.put("success", true);
      response.put("message", "Đăng ký thành công! Bạn đã có thể đăng nhập.");
      return ResponseEntity.ok(response);
    } else {
      response.put("success", false);
      response.put("message", "OTP không đúng hoặc đã hết hạn!");
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * Đăng nhập
   * Endpoint: POST /api/auth/login
   */
  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
    Map<String, Object> response = new HashMap<>();
    try {
      UserEntity user = userService.getUserByEmail(request.getEmail());
      if (user == null) {
        response.put("success", false);
        response.put("message", "Email không tồn tại");
        return ResponseEntity.badRequest().body(response);
      }
      if (!user.isEnabled()) {
        response.put("success", false);
        response.put("message", "Tài khoản chưa xác thực hoặc chưa hoàn tất đăng ký OTP!");
        return ResponseEntity.badRequest().body(response);
      }
      if (!user.getPasswordHash().equals(request.getPassword())) {
        response.put("success", false);
        response.put("message", "Mật khẩu không đúng");
        return ResponseEntity.badRequest().body(response);
      }

      response.put("success", true);
      response.put("message", "Đăng nhập thành công");
      response.put("user", Map.of(
          "id", user.getUserId(),
          "fullName", user.getFullName(),
          "email", user.getEmail(),
          "role", user.getRole(),
          "profilePictureUrl", user.getProfilePictureUrl() != null ? user.getProfilePictureUrl() : ""
      ));
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Đăng nhập thất bại: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * BƯỚC 1: Gửi OTP xác nhận đổi mật khẩu về email
   * Endpoint: POST /api/auth/forgot-password
   */
  @PostMapping("/forgot-password")
  public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody ForgotPasswordOtpRequest req) {
    userService.sendPasswordResetOtp(req.getEmail(), req.getNewPassword());
    return ResponseEntity.ok(Map.of(
        "success", true,
        "message", "Nếu email hợp lệ, mã OTP đã được gửi về email. Hãy kiểm tra hộp thư!"
    ));
  }

  /**
   * BƯỚC 2: Nhập OTP để đổi mật khẩu
   * Endpoint: POST /api/auth/reset-password-otp
   */
  @PostMapping("/reset-password-otp")
  public ResponseEntity<Map<String, Object>> resetPasswordOtp(@RequestBody VerifyOtpRequest req) {
    boolean ok = userService.verifyOtpAndResetPassword(req.getEmail(), req.getOtp());
    if (ok) {
      return ResponseEntity.ok(Map.of("success", true, "message", "Đổi mật khẩu thành công!"));
    } else {
      return ResponseEntity.badRequest().body(Map.of("success", false, "message", "OTP không đúng hoặc đã hết hạn!"));
    }
  }
}
