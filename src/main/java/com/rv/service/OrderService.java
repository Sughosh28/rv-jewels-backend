package com.rv.service;

import com.rv.dto.OrderDTO;
import com.rv.dto.OrderListDTO;
import com.rv.dto.ProductDTO;
import com.rv.dto.UserDTO;
import com.rv.jwt.JwtService;
import com.rv.model.OrderItems;
import com.rv.model.Orders;
import com.rv.model.Products;
import com.rv.model.UserEntity;
import com.rv.repository.OrderRepository;
import com.rv.repository.ProductRepository;
import com.rv.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Map<String, Object> createOrder(String token, Long productId, int quantity) {

        Long userId = jwtService.extractUserId(token);

        UserEntity userEntity = userRepository.findById(userId)
                .orElse(null);
        if (userEntity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        Products productEntity = productRepository.findById(productId)
                .orElse(null);
        if (productEntity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }

        Orders orderEntity = new Orders();
        BigDecimal price = productEntity.getPrice();
        BigDecimal discountedPrice = productEntity.getDiscountedPrice();
        BigDecimal discount = discountedPrice != null ? price.subtract(discountedPrice) : BigDecimal.ZERO;
        BigDecimal shippingCost = BigDecimal.valueOf(50);

        orderEntity.setUser(userEntity);
        orderEntity.setStatus(Orders.OrderStatus.PLACED);
        orderEntity.setDeliveryStatus(Orders.DeliveryStatus.PROCESSING);
        orderEntity.setOrderDate(LocalDateTime.now());
        orderEntity.setShippingCost(shippingCost);
        orderEntity.setDiscount(discount);

        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal subtotal = totalAmount.add(shippingCost).subtract(discount);

        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setSubtotal(subtotal);

        OrderItems orderItem = new OrderItems();
        orderItem.setOrder(orderEntity);
        orderItem.setProduct(productEntity);
        orderItem.setUser(userEntity);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(totalAmount);

        orderEntity.setOrderItems(Collections.singletonList(orderItem));
        Orders savedOrder = orderRepository.save(orderEntity);
        productEntity.setStockQuantity(productEntity.getStockQuantity() - quantity);
        productRepository.save(productEntity);

        UserDTO userDTO = new UserDTO(userEntity.getId(), userEntity.getUsername());
        ProductDTO productDTO = new ProductDTO(productEntity.getId(), productEntity.getName(), productEntity.getPrice(), productEntity.getDiscountedPrice(), quantity, productEntity.getDescription());

        OrderDTO orderDTO = new OrderDTO(savedOrder.getId(), userDTO, savedOrder.getStatus().toString(), savedOrder.getDeliveryStatus().toString(),
                savedOrder.getTotalAmount(), savedOrder.getShippingCost(), savedOrder.getDiscount(),
                savedOrder.getSubtotal(), List.of(productDTO), savedOrder.getOrderDate());

        return Map.of("message", "Order placed successfully", "order", orderDTO);
    }

    @Transactional
    public Map<String, Object> cancelOrder(String authToken, UUID orderId) {
        Long userId = jwtService.extractUserId(authToken);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!orderEntity.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to cancel this order");
        }

        orderEntity.setStatus(Orders.OrderStatus.CANCELLED);
        orderRepository.save(orderEntity);
        return Map.of("message", "Order canceled successfully", "orderId", orderId);
    }

    @Transactional
    public Map<String, Object> updateOrderStatus(String authToken, UUID orderId, Orders.OrderStatus status) {
        Long userId = jwtService.extractUserId(authToken);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderEntity.setStatus(status);

        orderRepository.save(orderEntity);

        return Map.of("message", "Order status updated successfully", "orderId", orderId, "newStatus", status);
    }

    @Transactional
    public Map<String, Object> getMyOrders(String authToken) {
        Long userId = jwtService.extractUserId(authToken);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Orders> ordersList = orderRepository.findByUser(userEntity);

        List<OrderListDTO> orderDTOList = ordersList.stream().map(this::convertToOrderDTO).collect(Collectors.toList());

        return Map.of("orders", orderDTOList);
    }

    private OrderListDTO convertToOrderDTO(Orders order) {
        UserDTO userDTO = new UserDTO(order.getUser().getId(), order.getUser().getUsername(), order.getUser().getEmail());

        List<ProductDTO> productDTOList = order.getOrderItems().stream().map(item ->
                new ProductDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getPrice(),
                        item.getProduct().getDiscountedPrice(),
                        item.getQuantity(),
                        item.getProduct().getDescription()
                )
        ).collect(Collectors.toList());

        return new OrderListDTO(
                order.getId(),
                userDTO,
                order.getTotalAmount(),
                order.getShippingCost(),
                order.getDiscount(),
                order.getSubtotal(),
                productDTOList,
                order.getOrderDate(),
                order.getStatus(),
                order.getDeliveryStatus()
        );
    }

    @Transactional
    public Map<String, Object> updateDeliveryStatus(String authToken, UUID orderId, Orders.DeliveryStatus status) {
        Long userId = jwtService.extractUserId(authToken);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderEntity.setDeliveryStatus(status);

        orderRepository.save(orderEntity);

        return Map.of("message", "Order delivery status updated successfully", "orderId", orderId, "newStatus", status);
    }

    public Map<String, Object> getOrderDetails(String authToken, UUID orderId) {
        Long userId = jwtService.extractUserId(authToken);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderListDTO orderDTO = convertToOrderListDTO(order);

        return Map.of("order", orderDTO);
    }

    private OrderListDTO convertToOrderListDTO(Orders order) {
        UserDTO userDTO = new UserDTO(
                order.getUser().getId(),
                order.getUser().getUsername(),
                order.getUser().getEmail()
        );

        List<ProductDTO> productDTOList = order.getOrderItems().stream().map(item ->

                new ProductDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getPrice(),
                        item.getProduct().getDiscountedPrice(),
                        item.getQuantity(),
                        item.getProduct().getDescription()
                )
        ).collect(Collectors.toList());

        return new OrderListDTO(
                order.getId(),
                userDTO,
                order.getTotalAmount(),
                order.getShippingCost(),
                order.getDiscount(),
                order.getSubtotal(),
                productDTOList,
                order.getOrderDate(),
                order.getStatus(),
                order.getDeliveryStatus()
        );
    }

}
