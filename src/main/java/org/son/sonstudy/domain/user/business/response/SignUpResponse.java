package org.son.sonstudy.domain.user.business.response;

import org.son.sonstudy.common.jwt.data.TokenInfo;

public record SignUpResponse(
        String accessToken,
        String refreshToken
) {
    public static SignUpResponse of(String  accessToken, String refreshToken) {
        return new SignUpResponse(accessToken, refreshToken);
    }

    public static SignUpResponse from(TokenInfo token) {
        return new SignUpResponse(token.accessToken(), token.refreshToken());
    }
}
