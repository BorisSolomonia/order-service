package com.boris.orderservice.controller;

import com.boris.orderservice.exception.CustomException;
import com.boris.orderservice.model.OrderDto;
import com.boris.orderservice.model.OrderRequest;
import com.boris.orderservice.model.OrderResponse;
import com.boris.orderservice.service.OrderService;
import jakarta.persistence.GeneratedValue;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest){
        Long orderId = orderService.placeOrder(orderRequest);
        System.out.println(orderRequest.getTotalAmount());
        log.info("Order Id: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @GetMapping("/getOrder/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id){
        log.info("Getting an order "+id);
        OrderDto orderDto = orderService.getOrder(id);
        log.info("the order"+id+" was found");
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

    @GetMapping("/{orderid}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderid){
        return new ResponseEntity<>(orderService.getOrderDetails(orderid), HttpStatus.OK);
    }

    @GetMapping("/demo")
    public String demo(){
        return "Hello";
    }

}
