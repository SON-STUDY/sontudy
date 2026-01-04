package org.son.sonstudy.common.jwt.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.config.data.JwtProperties;
import org.son.sonstudy.common.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtValidator {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties jwtProperties;
    private final JwtParser jwtParser;

    public boolean isValidFormat(final String tokenWithBearer) {
        return tokenWithBearer != null
                && tokenWithBearer.startsWith(BEARER_PREFIX)
                && tokenWithBearer.length() > BEARER_PREFIX.length();
    }

    public void verifyToken(final String token) {
        Jws<Claims> claims = jwtParser.parseClaims(token);

        verifyClaims(claims);
    }

    private void verifyClaims(final Jws<Claims> claims) {
        if (!isValidIssuer(claims)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_TOKEN);
        }

        if (isExpired(claims)) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
    }

    private boolean isValidIssuer(final Jws<Claims> claims) {
        String issuer = claims.getPayload().getIssuer();

        return Objects.equals(issuer, jwtProperties.issuer());
    }

    private boolean isExpired(final Jws<Claims> claims) {
        Date expiration = claims.getPayload().getExpiration();

        if (expiration == null) {
            return true;
        }

        return expiration.before(new Date());
    }
}
