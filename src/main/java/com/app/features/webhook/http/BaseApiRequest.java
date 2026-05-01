package com.app.features.webhook.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseApiRequest<T> {
    private String url;
    private HttpMethod method;
    private HttpHeaders headers;

    private T payload;
}
