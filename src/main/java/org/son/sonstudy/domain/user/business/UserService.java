package org.son.sonstudy.domain.user.business;

import lombok.RequiredArgsConstructor;
import org.son.sonstudy.domain.auth.business.AuthService;
import org.son.sonstudy.domain.user.business.response.SignUpResponse;
import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;
import org.son.sonstudy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final AuthService authService;

    @Transactional
    public void signUp(String name, String email, String rawPassword, Role role) {

        User savedUser = authService.signUp(name, email, rawPassword, role);
        userRepository.save(savedUser);
    }

    @Transactional
    public SignUpResponse login(String email, String rawPassword) {
        String accessToken = authService.login(email, rawPassword);

        return new SignUpResponse(accessToken);
    }
}
