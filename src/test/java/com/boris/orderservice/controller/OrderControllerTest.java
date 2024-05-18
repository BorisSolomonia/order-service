package com.boris.orderservice.controller;

import com.boris.orderservice.OrderServiceConfig;
import com.boris.orderservice.entity.OrderEntity;
import com.boris.orderservice.model.OrderDto;
import com.boris.orderservice.model.OrderRequest;
import com.boris.orderservice.model.PaymentMode;
import com.boris.orderservice.repository.OrderRepository;
import com.boris.orderservice.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
//import com.github.tomakehurst.wiremock.client.WireMock;
//import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
//import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

//import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.util.StreamUtils.copyToString;


@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = OrderServiceConfig.class)
public class OrderControllerTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MockMvc mockMvc;

//    @RegisterExtension
//    static WireMockExtension wireMockExtension =
//            WireMockExtension
//                    .newInstance()
//                    .options(WireMockConfiguration.wireMockConfig()
//                            .port(8080)).build();

    private ObjectMapper objectMapper =
            new ObjectMapper()
                    .findAndRegisterModules()
                    .configure(
                            SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS,
                            false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                            false);


//    @BeforeEach
//    void setup() throws IOException {
//        getProductDetailsResponse();
//        doPayment();
//        getPaymentDetails();
//        reduceQuantity();
//    }

//    private void getProductDetailsResponse() throws IOException {
//        wireMockExtension.stubFor(WireMock.get("/product")
//                .willReturn(WireMock.aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                        .withBody(
//                                copyToString(OrderControllerTest.class
//                                        .getClassLoader()
//                                        .getResourceAsStream("mock/GetProduct.json"),
//                                        Charset.defaultCharset()
//                                ))));
//    }

//    void doPayment(){
//        wireMockExtension.stubFor(WireMock.post("/dopayment")
//                .willReturn(WireMock.aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
//    }
//    void getPaymentDetails() throws IOException {
//        wireMockExtension.stubFor(WireMock.get(urlMatching("/payment/.*"))
//                .willReturn(WireMock.aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                        .withBody(
//                                copyToString(OrderControllerTest.class
//                                        .getClassLoader()
//                                        .getResourceAsStream("mock/GetPayment.json"),
//                                        Charset.defaultCharset()
//                                ))));
//    }
//    void reduceQuantity(){
//        wireMockExtension.stubFor(WireMock.put(urlMatching("/product/reduceQuantity/.*"))
//                .willReturn(WireMock.aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
//    }

    @Test
    public void test_WhenPlaceOrder_DoPayment_Success() throws Exception {
        OrderRequest orderRequest = mockOrderRequest();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("Customer")))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();
        System.out.println("Order Id: " + orderId);
        Optional<OrderEntity> order = orderRepository.findById(Long.valueOf(orderId));
        OrderEntity o = order.get();

        assertEquals(Long.parseLong(orderId), o.getId());
        assertEquals("PLACED", o.getOrderStatus());
        assertEquals(orderRequest.getTotalAmount(), o.getAmount());
        assertEquals(orderRequest.getQuantity(), o.getQuantity());


    }

    private OrderRequest mockOrderRequest() {
        return OrderRequest
                .builder()
                .paymentMode(PaymentMode.CASH)
                .productId(1L)
                .quantity(2L)
                .totalAmount(10L)
                .build();
    }
}
