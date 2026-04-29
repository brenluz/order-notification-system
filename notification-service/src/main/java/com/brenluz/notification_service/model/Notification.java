package com.brenluz.notification_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable=false)
    private Long orderId;

    @NotNull
    @Column(nullable=false)
    private String product;

    private String status;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}
