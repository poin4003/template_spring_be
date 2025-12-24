package com.template.app.core.messaging;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageHandlerRegistry {
    
    private final Map<String, MessageHandler<?>> handlers;

    public MessageHandler<?> get(String key) {
        MessageHandler<?> handler = handlers.get(key);
        if (handler == null) {
            throw new IllegalArgumentException("No handler for key: " + key);
        }
        return handler;
    }
}
