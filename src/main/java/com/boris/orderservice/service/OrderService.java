package com.boris.orderservice.service;

import com.boris.orderservice.model.OrderDto;
import com.boris.orderservice.model.OrderRequest;
import com.boris.orderservice.model.OrderResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderDto getOrder(Long id);



    OrderResponse getOrderDetails(Long orderid);
}
