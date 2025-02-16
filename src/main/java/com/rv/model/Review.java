package com.rv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rv.model.abstracts.BaseReview;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseReview {

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Products product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private Boolean verifiedPurchase;

    @ElementCollection
    private List<String> reviewImages;

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public List<String> getReviewImages() {
        return reviewImages;
    }

    public void setReviewImages(List<String> reviewImages) {
        this.reviewImages = reviewImages;
    }
}
