package com.rv.controller;

import com.rv.jwt.JwtService;
import com.rv.model.Orders;
import com.rv.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/admin")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;

    }

    @PatchMapping("/order-status-update/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@RequestHeader("Authorization") String token, @PathVariable UUID orderId, @RequestParam Orders.OrderStatus status) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(orderService.updateOrderStatus(authToken, orderId, status), HttpStatus.OK);
    }


    @PatchMapping("/order-deliver-status-update/{orderId}")
    public ResponseEntity<?> updateDeliveryStatus(@RequestHeader("Authorization") String token, @PathVariable UUID orderId, @RequestParam Orders.DeliveryStatus status) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(orderService.updateDeliveryStatus(authToken, orderId, status), HttpStatus.OK);
    }




}
