package com.brenluz.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String product;
    private String status;
    private Integer quantity;
    private Double price;
    private LocalDateTime createdAt;
}
