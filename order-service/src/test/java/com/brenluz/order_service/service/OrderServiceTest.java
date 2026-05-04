package com.brenluz.order_service.service;

import com.brenluz.order_service.dto.OrderRequest;
import com.brenluz.order_service.dto.OrderResponse;
import com.brenluz.order_service.model.Order;
import com.brenluz.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Tests are done using AAA pattern: Arrange, Act, Assert

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldSaveOrderAndReturnResponse() {
        // Arrange
        Order savedOrder = Order.builder()
                .product("product")
                .quantity(1)
                .price(10.0)
                .status("PENDING")
                .build();
        when(orderRepository.save(any())).thenReturn(savedOrder);
        // Act
        OrderResponse response = orderService.save(new OrderRequest("product", 1, 10.0));
        // Assert
        assertNotNull(response);
        assertEquals("product", response.getProduct());
        assertEquals(1, response.getQuantity());
        assertEquals(10.0, response.getPrice());
        assertEquals("PENDING", response.getStatus());
    }

}
