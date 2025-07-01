package com.hivmedical.medical.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyOtpRequest {
  @NotBlank(message = "Email không được để trống")
  private String email;

  @NotBlank(message = "OTP không được để trống")
  private String otp;

  public VerifyOtpRequest() {}
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getOtp() { return otp; }
  public void setOtp(String otp) { this.otp = otp; }
}
