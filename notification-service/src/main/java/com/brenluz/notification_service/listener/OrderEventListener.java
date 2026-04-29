package com.brenluz.notification_service.listener;

import com.brenluz.notification_service.config.RabbitMQConfig;
import com.brenluz.notification_service.model.Notification;
import com.brenluz.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderEventListener {
    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.queue)
    public void handleOrderEvent(Map<String, Object> orderData){
        Notification notification = Notification.builder()
                .orderId(Long.valueOf((Integer) orderData.get("id")))
                .product((String) orderData.get("product"))
                .build();
        notificationRepository.save(notification);
        System.out.println(notification);
    }
}
