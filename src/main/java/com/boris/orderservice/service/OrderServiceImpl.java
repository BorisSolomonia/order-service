package com.boris.orderservice.service;

import com.boris.orderservice.entity.OrderEntity;
import com.boris.orderservice.exception.CustomException;
import com.boris.orderservice.external.client.OrderPaymentService;
import com.boris.orderservice.external.client.OrderProductService;
import com.boris.orderservice.model.*;
import com.boris.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductService orderProductService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        log.info("Placing the Order Request: {}", orderRequest);
        OrderEntity orderEntity = buildOrderEntity(orderRequest);
        orderRepository.save(orderEntity);
        Map quantity = new HashMap();
        quantity.put("quantity",orderRequest.getQuantity());
        PaymentRequest paymentRequest = buildPaymentRequest(orderEntity, orderRequest);
        String orderStatus = makingPayment(paymentRequest);
        orderEntity.setOrderStatus(orderStatus);
        orderRepository.save(orderEntity);
        log.info("Order Status iiiiis: {}", orderStatus);
        log.info("Order orderRequest is: {}", orderRequest.getQuantity());
        reduceIfCreated(orderStatus,orderRequest,quantity);
        return orderEntity.getId();
    }

    @Override
    public OrderDto getOrder(Long id) {
        log.info("BORIS getting the object by Id...");
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(orderRepository.findById(id).get(), orderDto);
        return orderDto;
    }

    @Override
    public OrderResponse getOrderDetails(Long orderid) {
        OrderEntity orderEntity = orderRepository
                .findById(orderid).orElseThrow(()
                        -> new CustomException("order not found","NOT_FOUND",404));
        return OrderResponse
                .builder()
                .OrderId(orderid)
                .orderStatus(orderEntity.getOrderStatus())
                .orderDate(orderEntity.getOrderDate())
                .amount(orderEntity.getAmount())
                .productDTO(getProductDTO(orderEntity.getProductId()))
                .paymentResponse(getPayment(orderid))
                .build();
    }


    private PaymentRequest buildPaymentRequest(OrderEntity orderEntity, OrderRequest orderRequest){

        return PaymentRequest.builder()
                .paymentMode(orderRequest.getPaymentMode())
                .orderId(orderEntity.getId())
                .amount(orderRequest.getTotalAmount())
                .build();
    }

    private OrderEntity buildOrderEntity(OrderRequest orderRequest){

        return  OrderEntity
                .builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus(OrderStatus.PLACED.name())
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
    }

    private String makingPayment(PaymentRequest paymentRequest){
        String orderStatus = null;
        System.out.println(paymentRequest.toString());
        try{
            orderPaymentService.doPayment(paymentRequest);
            log.info("order placed successfully");
            orderStatus = OrderStatus.PLACED.name();
        }catch (Exception e){
            log.info("order placement FAILED");
            System.out.println("Exception is " + e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            orderStatus = OrderStatus.FAILED.name();
        }
        return orderStatus;
    }

    private void reduceIfCreated(String orderStatus, OrderRequest orderRequest, Map<String, Long> quantity){
        if(orderStatus.equals(OrderStatus.PLACED.name())){
            System.out.println("fffffffffffffffffffffffffff");
            orderProductService.reduceQuantity(orderRequest.getProductId(),quantity);
        }
    }

    private OrderResponse.ProductDTO getProductDTO(Long productId){
        return
         restTemplate
                .getForObject("http://PRODUCT-SERVICE/product/prod/"+ productId,
                        OrderResponse.ProductDTO.class);

    }

    private OrderResponse.PaymentResponse getPayment(Long orderId){
        System.out.println("sssssssssssssss111111sssssssssssssssssssssssssss");
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setPaymentResponse(
                restTemplate
                        .getForObject("http://PAYMENT-SERVICE/payment/get_payment_by_orderid/"+ orderId,
                                OrderResponse.PaymentResponse.class));

        System.out.println("ddddddddddddddddddddddd "+orderResponse.getPaymentResponse().getOrderId());
        return orderResponse.getPaymentResponse();
    }

}
