package org.son.sonstudy.domain.user.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.common.api.response.ApiResponse;
import org.son.sonstudy.domain.user.application.request.LoginRequest;
import org.son.sonstudy.domain.user.application.request.SignUpRequest;
import org.son.sonstudy.domain.user.business.response.SignUpResponse;
import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.business.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> signUpForCommonUser(
            @Valid @RequestBody SignUpRequest signUpRequest
    ) {
        userService.signUp(
                signUpRequest.name(),
                signUpRequest.email(),
                signUpRequest.password(),
                Role.USER
        );

        return ApiResponse.success(SuccessCode.SIGN_UP);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SignUpResponse>> login(
            @RequestBody LoginRequest loginRequest
    ) {
        SignUpResponse result = userService.login(
                loginRequest.email(),
                loginRequest.password()
        );

        return ApiResponse.success(SuccessCode.LOGIN, result);
    }
}
