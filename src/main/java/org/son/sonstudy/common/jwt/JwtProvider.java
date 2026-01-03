package org.son.sonstudy.common.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.config.data.JwtProperties;
import org.son.sonstudy.domain.user.model.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user) {
        return generateToken(user, jwtProperties.accessTokenExpireIn());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, jwtProperties.refreshTokenExpireIn());
    }

    private String generateToken(User user, long expiration) {
        long expiredAt = System.currentTimeMillis() + expiration;

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(expiredAt))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }
}
