package com.app.features.webhook.http;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookHttpClient {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    public <T> WebhookHttpResponse<T> send(BaseApiRequest<?> request, Class<T> responseType) {
        try {
            log.info("Sending Webhook [{}] to URL: {}", request.getMethod(), request.getUrl());

            ResponseEntity<String> response = restClient.method(Objects.requireNonNull(request.getMethod()))
                    .uri(Objects.requireNonNull(request.getUrl()))
                    .headers(h -> {
                        if (request.getHeaders() != null) {
                            h.addAll(Objects.requireNonNull(request.getHeaders()));
                        }
                    })
                    .body(Objects.requireNonNull(request.getPayload()))
                    .retrieve()
                    .toEntity(String.class);

            String body = response.getBody();
            T data = null;

            if (body != null && !body.isBlank() && responseType != Void.class) {
                data = objectMapper.readValue(body, responseType);
            }

            return new WebhookHttpResponse<>(response.getStatusCode().value(), body, data, false);

        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            String body = e.getResponseBodyAsString();
            log.warn("Partner API Error: {} - Body: {}", status, body);

            return new WebhookHttpResponse<>(status, body, null, false);

        } catch (ResourceAccessException e) {
            log.error("Network error: {}", e.getMessage());
            return new WebhookHttpResponse<>(null, e.getMessage(), null, true);

        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            return new WebhookHttpResponse<>(null, e.getMessage(), null, true);
        }
    }
}
