package com.brenluz.order_service.integration;

import com.brenluz.order_service.dto.OrderRequest;
import com.brenluz.order_service.dto.OrderResponse;
import com.brenluz.order_service.repository.OrderRepository;
import com.brenluz.order_service.service.OrderService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrderServiceIntegrationTests {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldSaveOrder(){
        // Arrange
        OrderRequest request = new OrderRequest("product", 1, 10.0);

        // Act
        OrderResponse response = orderService.save(request);

        // Assert
        assertNotNull(response);
        assertEquals("product", response.getProduct());
        assertEquals(1, response.getQuantity());
        assertEquals(10.0, response.getPrice());
        assertEquals("PENDING", response.getStatus());

        assertTrue(orderRepository.existsById(response.getId()));
    }

    @Test
    void shouldReturnAllSavedOrders() {
        // Arrange
        orderService.save(new OrderRequest("Laptop", 1, 999.99));
        orderService.save(new OrderRequest("Phone", 2, 499.99));

        // Act
        List<OrderResponse> orders = orderService.findAll();

        // Assert
        System.out.println(orders);
        assertEquals(2, orders.size());
        assertEquals("Laptop", orders.get(0).getProduct());
        assertEquals("Phone", orders.get(1).getProduct());
    }

    @Test
    void shouldSetStatusAndCreatedAtAutomatically() {
        // Arrange
        OrderRequest request = new OrderRequest("Laptop", 1, 999.99);

        // Act
        OrderResponse response = orderService.save(request);

        // Assert
        assertEquals("PENDING", response.getStatus());
        assertNotNull(response.getCreatedAt());
        assertTrue(response.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}

