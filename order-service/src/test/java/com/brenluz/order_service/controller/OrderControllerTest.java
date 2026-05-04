package com.brenluz.order_service.controller;

import com.brenluz.order_service.dto.OrderRequest;
import com.brenluz.order_service.dto.OrderResponse;
import com.brenluz.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.when;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void shouldCreateOrderAndReturn201() throws Exception {
        // Arrange
        OrderRequest order = new OrderRequest("product", 1, 10.0);
        OrderResponse expectedResponse = new OrderResponse();

        expectedResponse.setProduct("product");
        expectedResponse.setQuantity(1);
        expectedResponse.setPrice(10.0);
        expectedResponse.setStatus("PENDING");

        when(orderService.save(any(OrderRequest.class))).thenReturn(expectedResponse);

        // Act + Assert[[
        mockMvc.perform(post("/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product").value("product"));
    }

    @Test
    void shouldReturn400WhenNoProduct() throws Exception {
        // Arrange
        OrderRequest order = new OrderRequest(null, 1, 10.0);

        // Act + Assert
        mockMvc.perform(post("/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }

}
