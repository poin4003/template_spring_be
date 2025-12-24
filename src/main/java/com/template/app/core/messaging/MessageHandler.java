package com.template.app.core.messaging;

public interface MessageHandler<T> {
    void handle(T payload);
}
