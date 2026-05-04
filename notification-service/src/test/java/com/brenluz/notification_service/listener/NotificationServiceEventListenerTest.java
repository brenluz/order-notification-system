package com.brenluz.notification_service.listener;

import com.brenluz.notification_service.model.Notification;
import com.brenluz.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceEventListenerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Test
    void shouldSaveNotificationWhenReceiveingOrderEvent() {
        // Arrange
        Map<String, Object> notification = new HashMap<>();
        notification.put("id", 1);
        notification.put("product", "product");

        // Act
        orderEventListener.handleOrderEvent(notification);

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
