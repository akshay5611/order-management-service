package com.order.config;


import com.order.dto.OrderEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${order.kafka.topic}")
    private String ordersTopic;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic newTopic(){
        return TopicBuilder.name(ordersTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory () {
         Map<String, Object> config = new HashMap<>();
         config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
         config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
         config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  JsonSerializer.class);
         return new DefaultKafkaProducerFactory<>(config);
     }
    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, OrderEvent> orderEventConsumerFactory() {
        JsonDeserializer<OrderEvent> deserializer = new JsonDeserializer<>(OrderEvent.class);
        deserializer.addTrustedPackages("com.order.dto");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-management-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> orderEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderEventConsumerFactory());
        return factory;
    }
}
