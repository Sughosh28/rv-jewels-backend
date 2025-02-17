package com.rv.dto;

import java.io.Serializable;

public record ProductResponseDTO(Long id, String name, String description, java.math.BigDecimal price, Double averageRating,
                                 String productRetrievedSuccessfully) implements Serializable {}

