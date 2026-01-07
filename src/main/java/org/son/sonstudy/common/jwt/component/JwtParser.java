package org.son.sonstudy.common.jwt.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.jwt.data.UserContext;
import org.son.sonstudy.domain.user.model.Role;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtParser {
    private final SecretKey secretKey;

    public UserContext getUserContext(String token) {
        Claims claims = parseClaims(token).getPayload();

        String userId = claims.getSubject();
        String email = claims.get("email", String.class);

        String roleStr = claims.get("role", String.class);
        Role role = Role.valueOf(roleStr);

        return UserContext.of(userId, email, role);
    }

    public String getUserId(String token) {
        return parseClaims(token).getPayload()
                .getSubject();
    }

    public Jws<Claims> parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_FORMAT);
        }
    }
}
