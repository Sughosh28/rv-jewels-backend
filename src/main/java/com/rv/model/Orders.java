package com.rv.model;

import com.rv.model.abstracts.BaseOrder;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orders extends BaseOrder {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItems> orderItems;

    public enum DeliveryStatus {
        PROCESSING,
        PACKED,
        SHIPPED,
        IN_TRANSIT,
        OUT_FOR_DELIVERY,
        DELIVERED,
        FAILED,
        RETURNED
    }
    public enum OrderStatus {
        PLACED,
        CONFIRMED,
        PROCESSING,
        READY_TO_SHIP,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED,
        COMPLETED
    }
}
