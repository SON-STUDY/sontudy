package org.son.sonstudy.common.jwt.data;

public record TokenInfo(
        String accessToken,
        String refreshToken
) {
    public static TokenInfo of(String accessToken, String refreshToken) {
        return new TokenInfo(accessToken, refreshToken);
    }

    public static TokenInfo ofAdmin(String superToken){
        return new TokenInfo(superToken, null);
    }
}
