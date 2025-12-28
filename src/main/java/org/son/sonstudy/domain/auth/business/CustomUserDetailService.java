package org.son.sonstudy.domain.auth.business;

import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.domain.auth.model.CustomUserDetail;
import org.son.sonstudy.domain.user.model.User;
import org.son.sonstudy.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetail loadUserByUsername(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new CustomUserDetail(user);
    }
}
