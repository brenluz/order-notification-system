package com.brenluz.order_service.controller;

import com.brenluz.order_service.dto.OrderRequest;
import com.brenluz.order_service.dto.OrderResponse;
import com.brenluz.order_service.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management API")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @Valid
    @PostMapping
    public ResponseEntity<OrderResponse> save(@RequestBody @Valid OrderRequest order) {
        OrderResponse savedOrder = orderService.save(order);
        return ResponseEntity.status(201).body(savedOrder);
    }


    @Operation(summary = "Get all orders")
    @ApiResponse(responseCode = "200", description = "Sucess")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }
}
