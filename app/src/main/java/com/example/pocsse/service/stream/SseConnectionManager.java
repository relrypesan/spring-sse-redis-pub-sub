package com.example.pocsse.service.stream;

import com.example.pocsse.messaging.model.ProductStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseConnectionManager {

    private final Map<String, DataProductEmitter> connections = new ConcurrentHashMap<>();

    public SseEmitter addConnection(String productId, List<String> targetStatus, Long timeout) {
        SseEmitter emitter = new SseEmitter(timeout);

        emitter.onCompletion(() -> removeConnection(productId, "COMPLETION"));
        emitter.onTimeout(() -> removeConnection(productId, "TIMEOUT"));
        emitter.onError((e) -> removeConnection(productId, "ERROR: " + e.getMessage()));

        var dataProductEmitter = new DataProductEmitter(productId, targetStatus, timeout, emitter);

        log.info("[{}] - Nova conexão SSE adicionada para o produto: {} com targetStatus: {}", System.getenv("HOSTNAME"), productId, targetStatus);
        connections.put(productId, dataProductEmitter);
        return emitter;
    }

    private void removeConnection(String productId, String reason) {
        connections.remove(productId);
        log.info("[{}] - Conexão SSE removida para o produto: {}, motivo: {}", System.getenv("HOSTNAME"), productId, reason);
    }

    public void sendToClient(String productId, ProductStatusEvent event) {
        DataProductEmitter data = connections.get(productId);
        if (data != null) {
            try {
                log.info("[{}] - Enviando evento SSE do produto {} com status {}", System.getenv("HOSTNAME"), productId, event.newStatus());
                data.emitter().send(SseEmitter.event()
                    .id(String.valueOf(event.timestamp()))
                    .name("produto-atualizado")
                    .data(event));

                if (data.targetStatus() != null && data.targetStatus().contains(event.newStatus())) {
                    log.info("[{}] - Finalizando conexão SSE do produto: {} após envio do evento", System.getenv("HOSTNAME"), productId);
                    data.emitter().complete();
                } else {
                    log.info("[{}] - Evento SSE do produto {} com status {} mantendo conexão (não está na lista de targetStatus = {})",
                            System.getenv("HOSTNAME"), productId, event.newStatus(), data.targetStatus());
                }
            } catch (RuntimeException | IOException ex) {
                log.error("[{}] - Erro ao enviar evento SSE do produto: {}", System.getenv("HOSTNAME"), productId, ex);
                data.emitter().completeWithError(ex);
            }
        } else {
            log.warn("[{}] - Nenhuma conexão SSE encontrada do produto: {}", System.getenv("HOSTNAME"), productId);
        }
    }
}

