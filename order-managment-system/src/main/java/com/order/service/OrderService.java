package com.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.dto.CreateOrderRequest;
import com.order.dto.OrderEvent;
import com.order.dto.OrderResponse;
import com.order.entity.Order;
import com.order.entity.OrderStatus;
import com.order.exception.OrderNotFoundException;
import com.order.kafka.OrderEventProducer;
import com.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final ObjectMapper objectMapper;
    public OrderService(OrderRepository orderRepository,  OrderEventProducer orderEventProducer){
        this.orderEventProducer = orderEventProducer;
        this.orderRepository = orderRepository;
        this.objectMapper = new ObjectMapper();
    }
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest){
        logger.info("Entering into method createOrder from OrderService class ");
        Order order = new Order();
        //order.setId(UUID.randomUUID());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setCustomerId(createOrderRequest.getCustomerId());
        order.setTotalAmount(createOrderRequest.getTotalAmount());
        order.setCreateDate(OffsetDateTime.now());
        order.setUpdateDate(OffsetDateTime.now());
        try {
            order.setItemJson(objectMapper.writeValueAsString(createOrderRequest.getItems()));
        } catch (Exception exception){
            throw  new RuntimeException("Failed to serialize items " + exception.getMessage());
        }
       Order saveOrder =  orderRepository.save(order);

        //now publish events
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderId(saveOrder.getId());
        orderEvent.setEventTime(saveOrder.getCreateDate());
        orderEvent.setTotalAmount(saveOrder.getTotalAmount());
        orderEvent.setStatus(saveOrder.getOrderStatus());
        orderEvent.setItemJson(saveOrder.getItemJson());
        orderEvent.setCustomerId(saveOrder.getCustomerId());
        orderEventProducer.publish(orderEvent);
        return orderResponse(saveOrder);
    }

    private OrderResponse orderResponse(Order o) {
        logger.info("Entering into method orderResponse from OrderService class ");
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(o.getId());
        orderResponse.setCustomerId(o.getCustomerId());
        orderResponse.setTotalAmount(o.getTotalAmount());
        orderResponse.setStatus(o.getOrderStatus());
        orderResponse.setCreatedAt(o.getCreateDate());
        orderResponse.setUpdatedAt(o.getUpdateDate());
        try {
            List<CreateOrderRequest.OrderItemDto> items =   objectMapper.readValue(o.getItemJson(), new TypeReference<>() {});
            orderResponse.setItems(items);
        } catch (Exception ex){
            logger.error("unable to read object when trying deserialize " + ex.getMessage());
            throw new RuntimeException("error in OrderService class " + ex.getMessage());
        }
        return orderResponse;
    }

    public Optional<OrderResponse> getOrder(UUID id) {
        logger.info("Entering into method getOrder from OrderService class for id  " + id);
        return orderRepository.findById(id).map(this::orderResponse);
    }

    public List<OrderResponse> listOrders() {
        logger.info("Entering into method listOrders from OrderService class for id");
        return orderRepository.findAll().stream().map(this::orderResponse).collect(Collectors.toList());
    }

    public OrderResponse updateStatus(UUID id, OrderStatus status) {
        logger.info("Entering into method updateStatus from OrderService class for id  " + id);
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setOrderStatus(status);
        order.setUpdateDate(OffsetDateTime.now());
        return orderResponse(orderRepository.save(order));
    }

}
