package com.example.pocsse.messaging.publisher;

import com.example.pocsse.messaging.model.ProductStatusEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductStatusPublisher {

    public static final String CHANNEL = "product-status-updates";

    private final ObjectMapper mapper = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;

    public ProductStatusPublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishStatusChange(String productId, String newStatus) {
        ProductStatusEvent event = new ProductStatusEvent(
                productId,
                newStatus,
                System.currentTimeMillis()
        );

        try {
            String json = mapper.writeValueAsString(event);
            redisTemplate.convertAndSend(CHANNEL, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar event para JSON", e);
        }
    }
}

