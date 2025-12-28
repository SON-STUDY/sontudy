package org.son.sonstudy.common.jwt;

import io.jsonwebtoken.*;
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
    
    // TO DO: 리프레쉬 토큰도 만들어야 할까요

    private String generateToken(User user, long expiration) {
        long expiredAt = System.currentTimeMillis() + expiration;

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(expiredAt))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }
}
