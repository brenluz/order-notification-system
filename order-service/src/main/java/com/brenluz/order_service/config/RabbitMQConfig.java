package com.brenluz.order_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String queue = "order.queue";

    public static final String exchange = "order.exchange";

    public static final String routingKey = "order.routingkey";

    @Bean
    public Queue orderQueue() {
        return new Queue(queue);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, Exchange orderExchange) {
        return BindingBuilder
                .bind(orderQueue)
                .to((TopicExchange) orderExchange)
                .with(routingKey);
    }
}
