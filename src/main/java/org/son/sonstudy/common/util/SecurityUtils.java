package org.son.sonstudy.common.util;

import org.son.sonstudy.domain.auth.model.CustomUserDetail;
import org.son.sonstudy.domain.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetail customUserDetail) {
            return customUserDetail.getUser();
        }
        if (principal instanceof User) {
            return (User) principal;
        }
        if (principal instanceof UserDetails userDetails && userDetails instanceof CustomUserDetail) {
            return ((CustomUserDetail) userDetails).getUser();
        }

        return null;
    }
}
