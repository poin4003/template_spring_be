package com.app.core.mq.handler;

public interface MessageHandler<T> {
    void handle(T payload);
}
