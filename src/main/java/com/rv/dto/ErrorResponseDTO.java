package com.rv.dto;

public record ErrorResponseDTO (
         int statusCode,
         String message,
         long timestamp
){}


