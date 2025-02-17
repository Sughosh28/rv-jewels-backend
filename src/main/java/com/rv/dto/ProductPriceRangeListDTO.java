package com.rv.dto;

import java.util.List;
import java.io.Serializable;

public record ProductPriceRangeListDTO(
        double minPrice,
        double maxPrice,
        int totalProducts,
        List<ProductResponseDTO> products
) implements Serializable {}

