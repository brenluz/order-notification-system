package com.brenluz.order_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable=false)
    private String product;

    @NotNull
    @Column(nullable=false)
    private int quantity;

    @NotNull
    @Column(nullable=false)
    private double price;

    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}
