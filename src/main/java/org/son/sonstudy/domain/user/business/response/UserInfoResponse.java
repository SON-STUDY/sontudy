package org.son.sonstudy.domain.user.business.response;

import org.son.sonstudy.domain.user.model.User;

public record UserInfoResponse(
        String name,
        String email,
        String address,
        String role
) {
    public static UserInfoResponse of(User user) {
        return new UserInfoResponse(
                user.getName(),
                user.getEmail(),
                user.getAddress(),
                user.getRole().name()
        );
    }
}
