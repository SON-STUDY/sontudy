package org.son.sonstudy.domain.auth.business;

import org.son.sonstudy.domain.auth.business.response.OAuthUserInfo;

public interface OAuthService {
    boolean supports(String provider);

    OAuthUserInfo getOrCreateUser(String accessToken);
}
