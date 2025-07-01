package com.hivmedical.medical.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordOtpRequest {
  @NotBlank(message = "Email không được để trống")
  private String email;

  @NotBlank(message = "Mật khẩu mới không được để trống")
  private String newPassword;

  public ForgotPasswordOtpRequest() {}
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getNewPassword() { return newPassword; }
  public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
