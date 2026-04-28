package com.brenluz.order_service.controller;

import com.brenluz.order_service.model.Order;
import com.brenluz.order_service.service.OrderService;

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
    public ResponseEntity<Order> save(@RequestBody Order order) {
        Order savedOrder = orderService.save(order);
        return ResponseEntity.status(201).body(savedOrder);
    }

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }
}
