package com.example.pocsse.messaging.subscriber;

import com.example.pocsse.service.stream.SseConnectionManager;
import com.example.pocsse.messaging.model.ProductStatusEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import static com.example.pocsse.messaging.publisher.ProductStatusPublisher.CHANNEL;

@Slf4j
@Service
public class ProductStatusSubscriber implements MessageListener {

    private final ObjectMapper mapper = new ObjectMapper();
    private final SseConnectionManager connectionManager;

    public ProductStatusSubscriber(
            RedisMessageListenerContainer container,
            SseConnectionManager connectionManager
    ) {
        this.connectionManager = connectionManager;

        // inscreve no canal
        container.addMessageListener(this, new ChannelTopic(CHANNEL));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String jsonString = mapper.readValue(message.getBody(), String.class);
            ProductStatusEvent event = mapper.readValue(jsonString, ProductStatusEvent.class);

            log.info("[{}] - Evento recebido do Redis PUB/SUB para o produto {} com novo status {}",
                    System.getenv("HOSTNAME"), event.productId(), event.newStatus());

            // Envia para cliente, SE esta instância tiver a SSE
            connectionManager.sendToClient(event.productId(), event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

