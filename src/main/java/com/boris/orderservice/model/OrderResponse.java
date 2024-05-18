package com.boris.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long OrderId;
    private Instant orderDate;
    private String orderStatus;
    private Long amount;
    private ProductDTO productDTO;
    private PaymentResponse paymentResponse;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    // ეს სანამ სტატიკად არ გადავაკეთე მანამდე ერორს აგდებდა რეტთემფლეითი RestTemplate-ით გამოძახება,
    // ეს იმიტომ ხდებოდა რომ როდესაც ინერ კლასს იძახებდა რესტი, ის ვე აიინტანცირებდა instantiate-ს ვერ უკეთებდა
    // რადგან შიდა კლასია ეს და შესაბამისი აუთერ კლასის ობჯექტი უნდა არსებობდეს ეს რო ააწყო თუ ინერი არ არის სტატიკი
    public static class ProductDTO {

        private Long productId;
        private String name;
        private Long price;
        private Long quantity;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentResponse {

        private Long paymentId;
        private String status;
        private PaymentMode paymentMode;
        private Long amount;
        private Instant paymentDate;
        private Long orderId;

    }

}
