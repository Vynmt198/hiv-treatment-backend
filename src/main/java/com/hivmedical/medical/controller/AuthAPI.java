package com.hivmedical.medical.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hivmedical.medical.dto.ForgotPasswordOtpRequest;
import com.hivmedical.medical.dto.LoginRequest;
import com.hivmedical.medical.dto.RegisterRequest;
import com.hivmedical.medical.dto.VerifyOtpRequest;
import com.hivmedical.medical.entitty.UserEntity;
import com.hivmedical.medical.repository.UserRepositoty;
import com.hivmedical.medical.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthAPI {

  @Autowired
  private UserService userService;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private UserRepositoty userRepositoty;
  /**
   * BƯỚC 1: Đăng ký - Gửi OTP về email
   * Endpoint: POST /api/auth/register-request
   */

  @Operation(summary = "Register a new user by sending OTP to email")
  @ApiResponse(responseCode = "200", description = "OTP sent successfully",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Map.class, example = "{\"success\": true, \"message\": \"OTP đã được gửi về email. Vui lòng kiểm tra email để xác thực!\"}")))
  @ApiResponse(responseCode = "400", description = "Invalid input or email already exists",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Map.class, example = "{\"success\": false, \"message\": \"Email đã được sử dụng\"}")))
  @PostMapping("/register")
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
      String hashPass = passwordEncoder.encode(request.getPassword());
      user.setPasswordHash(hashPass); // encode crypt password
      user.setUsername(request.getEmail());
      user.setRole("member");
      userService.registerUserWithOtp(user);
      userRepositoty.save(user);
      // save user but enable not yet update


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
      Optional<UserEntity> userOptional = userRepositoty.findByEmail(request.getEmail());
      if (userOptional.isPresent()) {
        UserEntity user = userOptional.get();
        if (user.isEnabled()) {
          response.put("success", false);
          response.put("message", "Tài khoản đã được kích hoạt!");
          return ResponseEntity.badRequest().body(response);
        }

        user.setEnabled(true); // Update only the enable field
        userRepositoty.save(user); // Save updates (JPA merges, doesn't override)
      }
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

      if (!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())) {
        response.put("success", false);
        response.put("message", "Mật khẩu không đúng");
        return ResponseEntity.badRequest().body(response);
      }
      Algorithm algorithm = Algorithm.HMAC256("secret");
      String jwt = JWT.create().withClaim("username",user.getUsername())
          .withClaim("role",user.getRole()).sign(algorithm);
      response.put("success", true);
      response.put("message", "Đăng nhập thành công");
      response.put("token",jwt);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Đăng nhập thất bại: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }


/* =========== route verify phan quyen tam ========== */
  @Parameters({
      @Parameter(
          in = ParameterIn.HEADER,
          name = "Authorization",
          schema = @Schema(type = "string"),
          description = "Bearer token for authentication",
          required = true // Set to false if optional
      )
  })

  /* verify user already exist */
  @PostMapping("/verify")
  public ResponseEntity<String> verify(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
    System.out.println(auth);
    DecodedJWT jwt = JWT.require(Algorithm.HMAC256("secret")).build().verify(auth);
    String usernameByToken = jwt.getClaim("username").asString();
    System.out.println(usernameByToken);
    if(!userRepositoty.existsByUsername(usernameByToken)) {
      return ResponseEntity.badRequest().body("Cook");
    };


    System.out.println(jwt.getClaim("role").asString());

    return ResponseEntity.ok(auth);

  }
//=====================================================

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
