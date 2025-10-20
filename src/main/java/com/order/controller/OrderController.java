package com.order.controller;

import com.order.dto.CreateOrderRequest;
import com.order.dto.OrderResponse;
import com.order.entity.OrderStatus;
import com.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/create")
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
       logger.info("Entering into create for customer ID " + request.getCustomerId().toString());
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable UUID id) {
        Optional<OrderResponse> order = orderService.getOrder(id);
        if(order.isPresent()){
            return ResponseEntity.ok(order.get());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Not Found");
        response.put("message", "Order not found for ID: " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @GetMapping
    public ResponseEntity<List<OrderResponse>> list() {
        return ResponseEntity.ok(orderService.listOrders());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable UUID id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

}