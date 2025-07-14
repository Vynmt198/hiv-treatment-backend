package com.hivmedical.medical.controller;

import com.hivmedical.medical.service.MomoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

  @Autowired
  private MomoPaymentService momoPaymentService;

  @PostMapping("/momo")
  public Map<String, String> createMomoPayment(@RequestParam String orderId,
      @RequestParam String amount,
      @RequestParam String orderInfo,
      @RequestParam String redirectUrl,
      @RequestParam String ipnUrl) {

    //ipnUrl la de kiem tra trang thai tt
    //redirectUrl back url nhan payment
    // thong tin them ve don hjang
    // amount thif cu ep kieu ve string tu double to string
    //orderid gan id don hanbg vafo
    return momoPaymentService.createPaymentRequest(orderId, amount, orderInfo, redirectUrl, ipnUrl);
  }
}

