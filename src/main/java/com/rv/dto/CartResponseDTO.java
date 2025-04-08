package com.rv.dto;

import com.rv.model.Products;

public class CartResponseDTO {
    private Long id;
    private Integer quantity;
    private Products product;
    private UserBasicInfoDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public UserBasicInfoDTO getUser() {
        return user;
    }

    public void setUser(UserBasicInfoDTO user) {
        this.user = user;
    }
}
