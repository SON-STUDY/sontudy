package org.son.sonstudy.common.config.data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        String issuer,
        long accessTokenExpireIn,
        long refreshTokenExpireIn,
        @DefaultValue("false")
        boolean superTokenEnabled,
        long superTokenExpireIn
) {
}
