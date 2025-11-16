package com.example.pocsse.service.stream;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public record DataProductEmitter(
        String idProduct,
        List<String> targetStatus,
        Long timeout,
        SseEmitter emitter
) {
}
