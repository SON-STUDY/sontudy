package org.son.sonstudy.common.config;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.config.data.JwtProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationPropertiesScan("org.son.sonstudy.common.config.data")
@RequiredArgsConstructor
public class JwtConfig {
    private final JwtProperties jwtProperties;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}

