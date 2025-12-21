package com.template.app.config.jwt.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private long accessTokenExpirationMs;
    
    private long refreshTokenExpirationMs;
}
