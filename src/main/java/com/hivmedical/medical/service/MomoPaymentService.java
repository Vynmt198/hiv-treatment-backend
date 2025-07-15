package com.hivmedical.medical.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

  private final String momoEndpoint = "https://test-payment.momo.vn/v2/gateway/api/create"; // Đổi sang production nếu
                                                                                            // cần

  public String getPayUrl(String orderId, String amount, String orderInfo, String redirectUrl, String ipnUrl) {
    Map<String, String> requestData = createPaymentRequest(orderId, amount, orderInfo, redirectUrl, ipnUrl);

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, String>> request = new HttpEntity<>(requestData, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(momoEndpoint, request, Map.class);

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      Object payUrlObj = response.getBody().get("payUrl");
      if (payUrlObj != null) {
        return payUrlObj.toString();
      } else {
        // Nếu MoMo trả về lỗi, trả về message lỗi rõ ràng
        Object message = response.getBody().get("message");
        throw new RuntimeException("MoMo error: " + (message != null ? message.toString() : "Unknown error"));
      }
    } else {
      String errorBody = response.getBody() != null ? response.getBody().toString() : "No response body";
      throw new RuntimeException(
          "Failed to create MoMo payment request. Status: " + response.getStatusCode() + ", Body: " + errorBody);
    }
  }

  private Map<String, String> createPaymentRequest(String orderId, String amount, String orderInfo, String redirectUrl,
      String ipnUrl) {
    Map<String, String> requestData = new HashMap<>();
    requestData.put("partnerCode", partnerCode);
    requestData.put("accessKey", accessKey);
    requestData.put("orderId", orderId);
    requestData.put("requestId", orderId); // requestId thường giống orderId
    requestData.put("amount", amount);
    requestData.put("orderInfo", orderInfo);
    requestData.put("redirectUrl", redirectUrl);
    requestData.put("ipnUrl", ipnUrl);
    requestData.put("extraData", "");
    requestData.put("requestType", "captureWallet");
    requestData.put("lang", "vi");

    String signature = generateSignature(requestData);
    requestData.put("signature", signature);

    return requestData;
  }

  private String generateSignature(Map<String, String> data) {
    try {
      String rawData = "accessKey=" + data.get("accessKey") +
          "&amount=" + data.get("amount") +
          "&extraData=" + data.get("extraData") +
          "&ipnUrl=" + data.get("ipnUrl") +
          "&orderId=" + data.get("orderId") +
          "&orderInfo=" + data.get("orderInfo") +
          "&partnerCode=" + data.get("partnerCode") +
          "&redirectUrl=" + data.get("redirectUrl") +
          "&requestId=" + data.get("requestId") +
          "&requestType=" + data.get("requestType");

      System.out.println("[MoMo] rawData for signature: " + rawData);

      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
      sha256_HMAC.init(secretKeySpec);

      byte[] hash = sha256_HMAC.doFinal(rawData.getBytes());
      // MoMo yêu cầu signature dạng hex, không phải base64
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1)
          hexString.append('0');
        hexString.append(hex);
      }
      String signature = hexString.toString();
      System.out.println("[MoMo] signature: " + signature);
      return signature;
    } catch (Exception e) {
      throw new RuntimeException("Error generating MoMo signature", e);
    }
  }
}
