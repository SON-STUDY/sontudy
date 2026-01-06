package org.son.sonstudy.common.jwt.data;

import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;

public record UserContext(
        String userId,
        String email,
        Role role
) {
    public static UserContext of(String userId, String email, Role role) {
        return new UserContext(userId, email, role);
    }

    public static UserContext from(User user) {
        return new UserContext(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}
