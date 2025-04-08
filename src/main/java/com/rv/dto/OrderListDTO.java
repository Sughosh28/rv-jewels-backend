package com.rv.dto;


import com.rv.model.Orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderListDTO implements Serializable {
    private UUID id;
    private UserDTO userDTO;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private BigDecimal subtotal;
    private List<ProductDTO> orderItems;
    private LocalDateTime orderDate;
    private Orders.OrderStatus orderStatus;
    private Orders.DeliveryStatus deliveryStatus;

    public OrderListDTO(UUID id, UserDTO userDTO, BigDecimal totalAmount, BigDecimal shippingCost, BigDecimal discount,
                    BigDecimal subtotal, List<ProductDTO> orderItems, LocalDateTime orderDate,
                    Orders.OrderStatus orderStatus, Orders.DeliveryStatus deliveryStatus) {
        this.id = id;
        this.userDTO = userDTO;
        this.totalAmount = totalAmount;
        this.shippingCost = shippingCost;
        this.discount = discount;
        this.subtotal = subtotal;
        this.orderItems = orderItems;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.deliveryStatus = deliveryStatus;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public List<ProductDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<ProductDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Orders.OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Orders.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Orders.DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Orders.DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}

