package com.template.app.core.messaging.handler;

public interface MessageHandler<T> {
    void handle(T payload);
}
