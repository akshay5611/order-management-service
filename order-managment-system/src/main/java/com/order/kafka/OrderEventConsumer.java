package com.order.kafka;

import com.order.controller.OrderController;
import com.order.dto.OrderEvent;
import com.order.entity.Order;
import com.order.entity.OrderStatus;
import com.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class OrderEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final OrderRepository orderRepository;
    public OrderEventConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "${order.kafka.topic}", groupId = "order-service-group", containerFactory = "orderEventKafkaListenerContainerFactory")
    public void consumeEvent(OrderEvent orderEvent){
        logger.info("Message started consuming for id  " + orderEvent.getOrderId());
          Optional<Order> order =  orderRepository.findById(orderEvent.getOrderId());
          if(order.isPresent()){
            Order ord = order.get();
            ord.setOrderStatus(OrderStatus.CONFIRMED);
            ord.setUpdateDate(OffsetDateTime.now());
            orderRepository.save(ord);
          }
    }
}
