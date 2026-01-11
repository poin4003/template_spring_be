package com.template.app.config.settings;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.esotericsoftware.kryo.serializers.FieldSerializer.NotNull;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private final Cors cors = new Cors();
    private final Jwt jwt = new Jwt();
    private final Security security = new Security();
    private final Mq mq = new Mq();

    @Data
    public static class Cors {
        private List<String> allowedOrigins;
    }

    @Data
    public static class Jwt {
        @Positive
        private long accessTokenExpirationMs;
        
        @Positive
        private long refreshTokenExpirationMs;
    }

    @Data
    public static class Security {
        @NotNull
        private CryptoAlgo algo = CryptoAlgo.HMAC;

        public enum CryptoAlgo {
            HMAC, RSA
        }
    }

    @Data
    public static class Mq {
        private Retry retry = new Retry();

        @Data
        public static class Retry {
            private Integer defaultMaxAttempts = 3;
            private Long defaultBackoffMs = 1000L;
            private Double defaultMultiplier = 1.0;
        }
    }
}
