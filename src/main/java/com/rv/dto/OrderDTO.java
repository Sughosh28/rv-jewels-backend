package com.rv.dto;


import com.rv.model.Orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDTO implements Serializable {
    private UUID id;
    private UserDTO userDTO;
    private String status;
    private String deliveryStatus;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private BigDecimal subtotal;
    private List<ProductDTO> orderItems;
    private LocalDateTime orderDate;

    public OrderDTO(UUID id,UserDTO userDTO, String status, String deliveryStatus, BigDecimal totalAmount, BigDecimal shippingCost, BigDecimal discount, BigDecimal subtotal, List<ProductDTO> orderItems, LocalDateTime orderDate) {
        this.id = id;
        this.userDTO=userDTO;
        this.status = status;
        this.deliveryStatus = deliveryStatus;
        this.totalAmount = totalAmount;
        this.shippingCost = shippingCost;
        this.discount = discount;
        this.subtotal = subtotal;
        this.orderItems = orderItems;
        this.orderDate = orderDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
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
}

