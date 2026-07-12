package org.son.sonstudy.domain.user.application;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
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

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 정보 조회", description = "로그인된 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - JWT 토큰이 없거나 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/info")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<UserInfoResponse>> getUserInfo(
            @AuthenticationPrincipal UserContext userContext
    ) {
        String userId = userContext.userId();

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.OK, userService.getUserInfo(userId));
    }

    @Operation(summary = "일반 사용자 회원가입", description = "일반 사용자(USER) 계정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    @PostMapping
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<Void>> signUpForCommonUser(
            @Valid @RequestBody SignUpRequest signUpRequest
    ) {
        userService.signUp(
                signUpRequest.name(),
                signUpRequest.email(),
                signUpRequest.password(),
                Role.USER
        );

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.SIGN_UP);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 액세스/리프레시 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<SignUpResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        SignUpResponse result = userService.login(
                loginRequest.email(),
                loginRequest.password()
        );

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.LOGIN, result);
    }

    @Operation(summary = "판매자 신청", description = "현재 로그인된 사용자가 판매자 권한을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "판매자 신청 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - JWT 토큰이 없거나 유효하지 않음"),
            @ApiResponse(responseCode = "409", description = "이미 신청된 상태이거나 이미 판매자 권한 보유")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/seller-application")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<Void>> applyForSeller(
            @AuthenticationPrincipal UserContext userContext
            // TO DO: 신청에 필요한 데이터는 추후 개발
    ) {
        userService.applyForSeller(userContext.userId());
        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.SELLER_APPLICATION_OK);
    }
}
