package com.brenluz.order_service.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String queue = "order.queue";

    public static final String exchange = "order.exchange";

    public static final String routingKey = "order.routingkey";
}
