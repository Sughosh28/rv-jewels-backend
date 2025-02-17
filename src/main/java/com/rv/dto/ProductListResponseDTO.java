package com.rv.dto;

import java.util.List;
import java.io.Serializable;

public record ProductListResponseDTO(String category, int totalProducts, List<ProductResponseDTO> products
) implements Serializable {}

