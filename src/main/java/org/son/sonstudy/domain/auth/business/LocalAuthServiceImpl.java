package org.son.sonstudy.domain.auth.business;

import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.jwt.component.JwtProvider;
import org.son.sonstudy.common.jwt.data.TokenInfo;
import org.son.sonstudy.domain.auth.model.CustomUserDetail;
import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;
import org.son.sonstudy.domain.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalAuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public TokenInfo login(String email, String rawPassword) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );

        CustomUserDetail tryUser = (CustomUserDetail) authentication.getPrincipal();
        User authUser = tryUser.getUser();

        String accessToken = jwtProvider.generateAccessToken(authUser);
        String refreshToken = jwtProvider.generateRefreshToken(authUser);

        return TokenInfo.of(accessToken, refreshToken);
    }

    @Override
    public User signUp(String name, String email, String rawPassword, Role role) {
        String encodedPassword = createPassword(rawPassword);

        if(userRepository.existsUserByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
        }

        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .role(role)
                .build();
    }

    private String createPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
