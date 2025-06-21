package com.hivmedical.medical.service;

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
      message.setText("Chào bạn,\n\nMã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 10 phút.\n\nTrân trọng,\nMedicalHIV Team");
      message.setFrom("vynmtse181954@fpt.edu.vn");
      mailSender.send(message);
      logger.info("OTP email sent successfully to: {}", to);
    } catch (Exception e) {
      logger.error("Failed to send OTP email to {}: {}", to, e.getMessage());
      throw new RuntimeException("Failed to send email", e);
    }
  }
}