package com.rv.dto;

import com.rv.model.Products;

import java.io.Serializable;
import java.util.List;

public record ProductSearchResponse(int totalResults, List<Products> products, String searchTerm, String message) implements Serializable {}
