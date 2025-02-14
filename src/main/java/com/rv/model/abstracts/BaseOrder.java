package com.rv.model.abstracts;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseOrder {
    @Id
    @GeneratedValue
    private UUID id;

    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private BigDecimal subtotal;
    private BigDecimal discount;

}
