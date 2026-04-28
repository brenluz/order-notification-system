package com.brenluz.order_service.service;

import com.brenluz.order_service.config.RabbitMQConfig;
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
    public Order save(Order order) {
        Order savedOrder = orderRepository.save(order);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.exchange,
                RabbitMQConfig.routingKey,
                savedOrder
        );
        return savedOrder;
    }

    @Transactional
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
