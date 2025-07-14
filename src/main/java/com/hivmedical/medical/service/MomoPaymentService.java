package com.hivmedical.medical.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class MomoPaymentService {
  @Value("${momo.partnerCode}")
  private String partnerCode;

  @Value("${momo.accessKey}")
  private String accessKey;

  @Value("${momo.secretKey}")
  private String secretKey;


  public Map<String, String> createPaymentRequest(String orderId, String amount, String orderInfo, String redirectUrl, String ipnUrl) {
    Map<String, String> requestData = new HashMap<>();
    requestData.put("partnerCode", partnerCode);
    requestData.put("accessKey", accessKey);
    requestData.put("orderId", orderId);
    requestData.put("amount", amount);
    requestData.put("orderInfo", orderInfo);
    requestData.put("redirectUrl", redirectUrl);
    requestData.put("ipnUrl", ipnUrl);
    requestData.put("requestType", "captureWallet");
    String signature = generateSignature(requestData);
    requestData.put("signature", signature);
    return requestData;
  }


  private String generateSignature(Map<String, String> data) {
    try {
      String rawData = "accessKey=" + data.get("accessKey") +
          "&amount=" + data.get("amount") +
          "&ipnUrl=" + data.get("ipnUrl") +
          "&orderId=" + data.get("orderId") +
          "&orderInfo=" + data.get("orderInfo") +
          "&partnerCode=" + data.get("partnerCode") +
          "&redirectUrl=" + data.get("redirectUrl") +
          "&requestType=" + data.get("requestType");
      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
      sha256_HMAC.init(secret_key);
      byte[] hash = sha256_HMAC.doFinal(rawData.getBytes());
      return Base64.getEncoder().encodeToString(hash);
    } catch (Exception e) {
      throw new RuntimeException("Error generating MoMo signature", e);
    }
  }
}

