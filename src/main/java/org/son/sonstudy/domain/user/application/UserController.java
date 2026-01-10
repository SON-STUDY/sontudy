package org.son.sonstudy.domain.user.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.common.api.response.ApiResponse;
import org.son.sonstudy.common.jwt.data.UserContext;
import org.son.sonstudy.domain.user.application.request.LoginRequest;
import org.son.sonstudy.domain.user.application.request.SignUpRequest;
import org.son.sonstudy.domain.user.business.UserService;
import org.son.sonstudy.domain.user.business.response.SignUpResponse;
import org.son.sonstudy.domain.user.business.response.UserInfoResponse;
import org.son.sonstudy.domain.user.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
            @AuthenticationPrincipal UserContext userContext
    ) {
        String userId = userContext.userId();

        return ApiResponse.success(SuccessCode.OK, userService.getUserInfo(userId));
    }

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
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        SignUpResponse result = userService.login(
                loginRequest.email(),
                loginRequest.password()
        );

        return ApiResponse.success(SuccessCode.LOGIN, result);
    }

    @PostMapping("/seller-application")
    public ResponseEntity<ApiResponse<Void>> applyForSeller(
            @AuthenticationPrincipal UserContext userContext
            // TO DO: 신청에 필요한 데이터는 추후 개발
    ) {
        userService.applyForSeller(userContext.userId());
        return ApiResponse.success(SuccessCode.SELLER_APPLICATION_OK);
    }
}
