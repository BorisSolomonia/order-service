package com.boris.orderservice.service;

import com.boris.orderservice.entity.OrderEntity;
import com.boris.orderservice.exception.CustomException;
import com.boris.orderservice.external.client.OrderPaymentService;
import com.boris.orderservice.external.client.OrderProductService;
import com.boris.orderservice.model.OrderRequest;
import com.boris.orderservice.model.OrderResponse;
import com.boris.orderservice.model.PaymentMode;
import com.boris.orderservice.model.PaymentRequest;
import com.boris.orderservice.repository.OrderRepository;
import com.sun.source.tree.ModuleTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductService orderProductService;

    @Mock
    private OrderPaymentService orderPaymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Test when order is successful")
    @Test
    void test_When_Order_Success() {
        OrderEntity order = getMockOrder();

        Mockito.when(orderRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(order));
        Mockito.when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/prod/"+ order.getProductId(),
                        OrderResponse.ProductDTO.class)
        ).thenReturn(getMockProductResponse());

        Mockito.when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/get_payment_by_orderid/"+ order.getId(),
                OrderResponse.PaymentResponse.class)).thenReturn(getMockPaymentResponse());

        orderService.getOrderDetails(1L);

        Mockito.verify(orderRepository, Mockito.times(1)).findById(anyLong());

        Mockito.verify(restTemplate, Mockito.times(1)).getForObject("http://PRODUCT-SERVICE/product/prod/"+ order.getProductId(),
                OrderResponse.ProductDTO.class);

        Mockito.verify(restTemplate, Mockito.times(1)).getForObject("http://PAYMENT-SERVICE/payment/get_payment_by_orderid/"+ order.getId(),
                OrderResponse.PaymentResponse.class);

        OrderResponse orderResponse = orderService.getOrderDetails(1L);

        assertNotNull(orderResponse);
        assertEquals(orderResponse.getOrderId(), order.getId());
    }


    @DisplayName("Test when order is not found")
    @Test
    void test_When_Get_Order_NOT_Found() {

        Mockito.when(orderRepository
                .findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception = assertThrows(CustomException.class, () -> {
            orderService.getOrderDetails(1L);
            });
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());

        Mockito.verify(orderRepository, Mockito.times(1)).findById(anyLong());
    }

    @DisplayName("Place order is successful")
    @Test
    void test_When_Place_Order_Success() {
        OrderEntity order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();
        Mockito.when(orderRepository.save(
                Mockito.any(OrderEntity.class)))
                .thenReturn(order);
        Mockito.when(orderProductService.reduceQuantity(
                Mockito.anyLong(), Mockito.anyMap()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        Long orderId = orderService.placeOrder(orderRequest);

//        Mockito.verify(orderRepository, Mockito.times(1)).save(Mockito.any(OrderEntity.class));
//        Mockito.verify(orderProductService, Mockito.times(1)).reduceQuantity(Mockito.anyLong(), Mockito.anyMap());
        assertEquals(order.getId(), orderId);
    }

    @DisplayName("Place order is failed")
    @Test
    void test_When_Place_Order_Fail() {
        OrderEntity order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();
        Mockito.when(orderRepository.save(
                        Mockito.any(OrderEntity.class)))
                .thenReturn(order);
        Mockito.when(orderProductService.reduceQuantity(
                        Mockito.anyLong(), Mockito.anyMap()))
                .thenThrow(new RuntimeException());

        Long orderId = orderService.placeOrder(orderRequest);

//        Mockito.verify(orderRepository, Mockito.times(1)).save(Mockito.any(OrderEntity.class));
//        Mockito.verify(orderProductService, Mockito.times(1)).reduceQuantity(Mockito.anyLong(), Mockito.anyMap());
        assertEquals(order.getId(), orderId);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest
                .builder()
                .productId(1L)
                .quantity(1L)
                .totalAmount(100L)
                .paymentMode(PaymentMode.CASH)
                .build();
    }


    private OrderResponse.PaymentResponse getMockPaymentResponse() {
        return OrderResponse.PaymentResponse
                .builder()
                .orderId(1L)
                .paymentId(1L)
                .paymentMode(PaymentMode.CASH)
                .amount(100L)
                .paymentDate(Instant.now())
                .status("SUCCESS")
                .build();
    }

    private OrderResponse.ProductDTO getMockProductResponse() {
        return OrderResponse.ProductDTO
                .builder()
                .productId(1L)
                .name("Product 1")
                .price(100L)
                .build();
    }

    private OrderEntity getMockOrder() {
        return OrderEntity
                .builder()
                .orderDate(Instant.now())
                .orderStatus("CREATED")
                .productId(1L)
                .quantity(1L)
                .amount(100L)
                .build();

    }
}