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
    @Autowired
    private JwtService jwtService;

    @PostMapping("/place-order")
    public ResponseEntity<?> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"); // ✅ Let GlobalExceptionHandler handle it
        }

        String authToken = token.substring(7);
        return ResponseEntity.ok(orderService.createOrder(authToken, productId, quantity));
    }

    @DeleteMapping("/cancel-order/{orderId}")
    public ResponseEntity<?> cancelOrder(@RequestHeader("Authorization") String token, @PathVariable UUID orderId) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(orderService.cancelOrder(authToken, orderId), HttpStatus.OK);

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

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }

        String authToken = token.substring(7);
        return new ResponseEntity<>(orderService.getMyOrders(authToken), HttpStatus.OK);

    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@RequestHeader("Authorization") String token, @PathVariable UUID orderId) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authentication is missing");
        }
        String authToken = token.substring(7);
        return new ResponseEntity<>(orderService.getOrderDetails(authToken, orderId), HttpStatus.OK);
    }

}
