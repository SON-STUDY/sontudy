package org.son.sonstudy.domain.auth.business;

import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;

public interface AuthService {
    String login(String email, String rawPassword);

    User signUp(String name, String email, String rawPassword, Role role);
}
