package org.son.sonstudy.common.config.data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, String issuer, long accessTokenExpireIn) {
}
