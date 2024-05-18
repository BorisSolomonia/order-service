package com.boris.orderservice.external.client;

import com.boris.orderservice.exception.CustomException;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@FeignClient(name = "PRODUCT-SERVICE/product")
public interface OrderProductService {

    @PostMapping("/reducequantity/{id}")
    ResponseEntity<Void> reduceQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request);


}
