package com.rv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rv.model.abstracts.BaseProduct;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Products extends BaseProduct implements Serializable {

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private String clarity;
    private String color;


    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews;


    public enum ProductCategory {
        RINGS,
        NECKLACES,
        EARRINGS,
        BRACELETS,
        PENDANTS,
        BANGLES,
        CHAINS,
        ANKLETS,
        MANGALSUTRA,
        NOSE_RINGS,
        WEDDING_JEWELRY,
        MENS_JEWELRY,
        KIDS_JEWELRY,
        GEMSTONES,
        DIAMONDS,
        GOLD_COINS,
        SILVER_COINS
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public String getClarity() {
        return clarity;
    }

    public void setClarity(String clarity) {
        this.clarity = clarity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
