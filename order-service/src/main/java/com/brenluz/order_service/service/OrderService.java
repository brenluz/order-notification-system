package com.brenluz.order_service.service;

import com.brenluz.order_service.config.RabbitMQConfig;
import com.brenluz.order_service.dto.OrderRequest;
import com.brenluz.order_service.dto.OrderResponse;
import com.brenluz.order_service.model.Order;
import com.brenluz.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public OrderResponse save(OrderRequest request) {
        Order order = Order.builder()
                .product(request.getProduct())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();

        Order savedOrder = orderRepository.save(order);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.exchange,
                RabbitMQConfig.routingKey,
                savedOrder
        );
        return (new  OrderResponse(
                savedOrder.getId(),
                savedOrder.getProduct(),
                savedOrder.getStatus(),
                savedOrder.getQuantity(),
                savedOrder.getPrice(),
                savedOrder.getCreatedAt()
        ));
    }

    @Transactional
    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(o -> new OrderResponse(
                        o.getId(),
                        o.getProduct(),
                        o.getStatus(),
                        o.getQuantity(),
                        o.getPrice(),
                        o.getCreatedAt()
                ))
                .toList();
    }
}
