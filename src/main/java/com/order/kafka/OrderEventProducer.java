package com.order.kafka;

import com.order.dto.OrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private String topic;

    public  OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate, @Value("${order.kafka.topic}") String topic){
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }
    public void publish(OrderEvent orderEvent){
       kafkaTemplate.send(topic, String.valueOf(orderEvent.getOrderId()), orderEvent);
    }
}
