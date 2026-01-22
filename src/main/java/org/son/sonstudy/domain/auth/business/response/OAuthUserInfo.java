package org.son.sonstudy.domain.auth.business.response;

import org.son.sonstudy.domain.auth.model.OAuthProvider;

public record OAuthUserInfo(
        String oauthId,
        OAuthProvider oAuthProvider,
        String name
) {
}
