package com.boris.orderservice.external.client;

import com.boris.orderservice.exception.CustomException;
import com.boris.orderservice.model.PaymentRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE/payment")
public interface OrderPaymentService {
    @PostMapping("/dopayment")
    void doPayment(@RequestBody PaymentRequest paymentRequest);

}
