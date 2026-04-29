package com.brenluz.order_service.controller;

import com.brenluz.order_service.dto.OrderRequest;
import com.brenluz.order_service.dto.OrderResponse;
import com.brenluz.order_service.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderResponse> save(@RequestBody @Valid OrderRequest order) {
        OrderResponse savedOrder = orderService.save(order);
        return ResponseEntity.status(201).body(savedOrder);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }
}
