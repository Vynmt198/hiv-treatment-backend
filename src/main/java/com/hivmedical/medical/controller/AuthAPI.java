package com.hivmedical.medical.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hivmedical.medical.dto.ForgotPasswordOtpRequest;
import com.hivmedical.medical.dto.LoginRequest;
import com.hivmedical.medical.dto.RegisterRequest;
import com.hivmedical.medical.dto.VerifyOtpRequest;
import com.hivmedical.medical.entitty.Role;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.repository.UserRepositoty;
import com.hivmedical.medical.service.EmailService;
import com.hivmedical.medical.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthAPI {
  private static final Logger logger = LoggerFactory.getLogger(AuthAPI.class);

  @Autowired
  private UserService userService;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;
  @Autowired
  private UserRepositoty userRepository;
  @Autowired
  private EmailService emailService;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> registerRequest(@Valid @RequestBody RegisterRequest request, BindingResult result) {
    logger.info("Processing registration request for email: {}", request.getEmail());
    Map<String, Object> response = new HashMap<>();
    try {
      if (result.hasErrors()) {
        response.put("success", false);
        response.put("message", result.getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.badRequest().body(response);
      }
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
      user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
      user.setUsername(request.getEmail());
      user.setRole(Role.PATIENT);
      user.setEnabled(false);
      userService.registerUserWithOtp(user);
      userRepository.save(user);

      response.put("success", true);
      response.put("message", "OTP đã được gửi về email. Vui lòng kiểm tra email để xác thực!");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Registration failed for email {}: {}", request.getEmail(), e.getMessage());
      response.put("success", false);
      response.put("message", "Đăng ký thất bại: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/register-verify-otp")
  public ResponseEntity<Map<String, Object>> verifyRegisterOtp(@Valid @RequestBody VerifyOtpRequest request) {
    logger.info("Verifying OTP for email: {}", request.getEmail());
    Map<String, Object> response = new HashMap<>();
    try {
      boolean ok = userService.verifyOtpAndRegister(request.getEmail(), request.getOtp());
      if (ok) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isEnabled()) {
          response.put("success", false);
          response.put("message", "Tài khoản đã được kích hoạt!");
          return ResponseEntity.badRequest().body(response);
        }
        user.setEnabled(true);
        userRepository.save(user);
        response.put("success", true);
        response.put("message", "Đăng ký thành công! Bạn đã có thể đăng nhập.");
        return ResponseEntity.ok(response);
      } else {
        response.put("success", false);
        response.put("message", "OTP không đúng hoặc đã hết hạn!");
        return ResponseEntity.badRequest().body(response);
      }
    } catch (Exception e) {
      logger.error("OTP verification failed for email {}: {}", request.getEmail(), e.getMessage());
      response.put("success", false);
      response.put("message", "Xác thực OTP thất bại: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
    logger.info("Processing login request for email: {}", request.getEmail());
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
      if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        response.put("success", false);
        response.put("message", "Mật khẩu không đúng");
        return ResponseEntity.badRequest().body(response);
      }

      Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
      String jwt = JWT.create()
          .withClaim("username", user.getUsername())
          .withClaim("role", user.getRole().name())
          .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
          .sign(algorithm);

      response.put("success", true);
      response.put("message", "Đăng nhập thành công");
      response.put("token", jwt);
      response.put("role", user.getRole().name());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Login failed for email {}: {}", request.getEmail(), e.getMessage());
      response.put("success", false);
      response.put("message", "Đăng nhập thất bại: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/verify")
  public ResponseEntity<Map<String, Object>> verify(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
    logger.info("Verifying token: {}", auth);
    Map<String, Object> response = new HashMap<>();
    try {
        if (!auth.startsWith("Bearer ")) {
        response.put("success", false);
        response.put("message", "Định dạng token không hợp lệ");
        return ResponseEntity.status(401).body(response);
      }
      String token = auth.substring(7);
      Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
      JWTVerifier verifier = JWT.require(algorithm).build();
      DecodedJWT jwt = verifier.verify(token);

      String username = jwt.getClaim("username").asString();
      String role = jwt.getClaim("role").asString();
      if (!userRepository.existsByUsername(username)) {
        response.put("success", false);
        response.put("message", "Người dùng không tồn tại");
        return ResponseEntity.status(401).body(response);
      }
      response.put("success", true);
      response.put("username", username);
      response.put("role", role);
      return ResponseEntity.ok(response);
    } catch (JWTVerificationException e) {
      logger.error("Token verification failed: {}", e.getMessage());
      response.put("success", false);
      response.put("message", "Token không hợp lệ hoặc đã hết hạn: " + e.getMessage());
      return ResponseEntity.status(401).body(response);
    } catch (Exception e) {
      logger.error("Unexpected error during token verification: {}", e.getMessage());
      response.put("success", false);
      response.put("message", "Lỗi không xác định: " + e.getMessage());
      return ResponseEntity.status(500).body(response);
    }
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody ForgotPasswordOtpRequest req) {
    logger.info("Processing forgot password request for email: {}", req.getEmail());
    Map<String, Object> response = new HashMap<>();
    try {
      userService.sendPasswordResetOtp(req.getEmail(), req.getNewPassword());
      response.put("success", true);
      response.put("message", "Nếu email hợp lệ, mã OTP đã được gửi về email. Hãy kiểm tra hộp thư!");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Failed to send OTP for email {}: {}", req.getEmail(), e.getMessage());
      response.put("success", false);
      response.put("message", "Gửi OTP thất bại: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/reset-password-otp")
  public ResponseEntity<Map<String, Object>> resetPasswordOtp(@RequestBody VerifyOtpRequest req) {
    logger.info("Processing reset password OTP for email: {}", req.getEmail());
    try {
      boolean ok = userService.verifyOtpAndResetPassword(req.getEmail(), req.getOtp());
      if (ok) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Đổi mật khẩu thành công!"));
      } else {
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "OTP không đúng hoặc đã hết hạn!"));
      }
    } catch (Exception e) {
      logger.error("Reset password OTP failed for email {}: {}", req.getEmail(), e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Đổi mật khẩu thất bại: " + e.getMessage()));
    }
  }

  @PostMapping("/test-email")
  public ResponseEntity<Map<String, Object>> testEmail(@RequestBody Map<String, String> request) {
    logger.info("Processing test email request for: {}", request.get("email"));
    Map<String, Object> response = new HashMap<>();
    try {
      String email = request.get("email");
      if (email == null || email.trim().isEmpty()) {
        response.put("success", false);
        response.put("message", "Email is required");
        return ResponseEntity.badRequest().body(response);
      }
      emailService.sendOtpEmail(email, "TEST123");
      response.put("success", true);
      response.put("message", "Email sent successfully");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Failed to send test email: {}", e.getMessage());
      response.put("success", false);
      response.put("message", "Failed to send email: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }
}