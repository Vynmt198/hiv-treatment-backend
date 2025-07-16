package com.hivmedical.medical.service;

import java.time.LocalDate;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  @Autowired
  private JavaMailSender mailSender;

  public void sendOtpEmail(String to, String otp) {
    try {
      logger.info("Attempting to send OTP email to: {}", to);
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject("MedicalHIV - Xác thực OTP cho tài khoản của bạn");
      message.setText("Chào bạn,\n\nMã OTP của bạn là: " + otp
          + "\nMã này có hiệu lực trong 10 phút.\n\nTrân trọng,\nMedicalHIV Team");
      message.setFrom("vynmtse181954@fpt.edu.vn");
      mailSender.send(message);
      logger.info("OTP email sent successfully to: {}", to);
    } catch (Exception e) {
      logger.error("Failed to send OTP email to {}: {}", to, e.getMessage());
      throw new RuntimeException("Failed to send email", e);
    }
  }

  public void sendAppointmentConfirmation(String to, String referenceCode, LocalDate date, LocalTime time,
      String type) {
    try {
      logger.info("Sending appointment confirmation to: {}", to);
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject("MedicalHIV - Xác nhận lịch hẹn");
      message.setText(String.format(
          "Chào bạn,\n\nLịch hẹn của bạn đã được xác nhận:\n" +
              "Loại: %s\n" +
              "Ngày: %s\n" +
              "Giờ: %s\n" +
              "Mã tham chiếu: %s\n\n" +
              "Vui lòng lưu mã này để theo dõi lịch hẹn.\n\nTrân trọng,\nMedicalHIV Team",
          type, date, time, referenceCode));
      message.setFrom("your_email@gmail.com");
      mailSender.send(message);
      logger.info("Appointment confirmation email sent to: {}", to);
    } catch (Exception e) {
      logger.error("Failed to send appointment confirmation to {}: {}", to, e.getMessage());
      throw new RuntimeException("Failed to send email", e);
    }
  }

  public void sendAccountInfo(String toEmail, String fullName, String username, String password) {
    try {
      logger.info("Sending account info to: {}", toEmail);
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(toEmail);
      message.setSubject("MedicalHIV - Tài khoản đăng nhập hệ thống quản lý HIV");
      message.setText(String.format(
          "Xin chào %s,\n\nBạn đã được tạo tài khoản trên hệ thống quản lý HIV.\n\nThông tin đăng nhập:\nEmail: %s\nMật khẩu tạm thời: %s\n\nVui lòng đăng nhập và đổi mật khẩu sau lần đăng nhập đầu tiên.\n\nTrân trọng,\nMedicalHIV Team",
          fullName, username, password));
      message.setFrom("vynmtse181954@fpt.edu.vn");
      mailSender.send(message);
      logger.info("Account info email sent to: {}", toEmail);
    } catch (Exception e) {
      logger.error("Failed to send account info to {}: {}", toEmail, e.getMessage());
      throw new RuntimeException("Failed to send email", e);
    }
  }
}