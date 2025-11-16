package com.example.pocsse.controller;

import com.example.pocsse.service.stream.SseConnectionManager;
import com.example.pocsse.messaging.publisher.ProductStatusPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProdutoController {

    private final ProductStatusPublisher publisher;
    private final SseConnectionManager connectionManager;

    @PostMapping("/{id}/status/{status}")
    public ResponseEntity<?> changeStatus(@PathVariable String id, @PathVariable String status) {

        // salva no DynamoDB...

        log.info("Alterando status do produto {} para {}, publicando no redis PUB/SUB", id, status);
        publisher.publishStatusChange(id, status);

        return ResponseEntity.ok("Status atualizado para " + status);
    }

    @GetMapping(value = "/{productId}/events", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable String productId,
                                @RequestParam(name = "targetStatus", required = false) List<String> targetStatus,
                                @RequestParam(name = "timeout", required = false, defaultValue = "0") long timeout) {
        log.info("Cliente solicitou conexão SSE para o produto {} com targetStatus {} e timeout {} ms", productId, targetStatus, timeout);
        return connectionManager.addConnection(productId, targetStatus, timeout);
    }
}
