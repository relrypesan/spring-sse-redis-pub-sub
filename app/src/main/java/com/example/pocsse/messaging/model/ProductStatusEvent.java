package com.example.pocsse.messaging.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record ProductStatusEvent(
        @JsonProperty("productId") String productId,
        @JsonProperty("newStatus") String newStatus,
        @JsonProperty("timestamp") long timestamp
) implements Serializable {}

