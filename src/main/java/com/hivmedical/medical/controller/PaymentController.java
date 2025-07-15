package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.MomoPaymentRequest;
import com.hivmedical.medical.service.MomoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

  @Autowired
  private MomoPaymentService momoPaymentService;

  @PostMapping("/momo")
  public Map<String, String> createMomoPayment(@RequestBody MomoPaymentRequest req) {
    System.out.println("[PaymentController] redirectUrl nhận được: " + req.getRedirectUrl());
    System.out.println("[PaymentController] ipnUrl nhận được: " + req.getIpnUrl());
    String payUrl = momoPaymentService.getPayUrl(req.getOrderId(), req.getAmount(), req.getOrderInfo(),
        req.getRedirectUrl(), req.getIpnUrl());
    Map<String, String> response = new HashMap<>();
    response.put("payUrl", payUrl);
    return response;
  }

}
