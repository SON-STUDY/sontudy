package org.son.sonstudy.domain.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.domain.user.business.UserService;
import org.son.sonstudy.domain.user.business.response.SellerApplicationResponse;
import org.son.sonstudy.domain.user.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "관리자 API - ADMIN 권한 필요")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    private final UserService userService;

    @Operation(summary = "관리자 회원가입", description = "관리자 계정을 생성합니다. ADMIN 권한이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "ADMIN 권한 없음"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    @PostMapping
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<Void>> signUpForAdmin(
            @Valid @RequestBody SignUpAdminRequest signUpAdminRequest
    ) {
        userService.signUp(
                signUpAdminRequest.name(),
                signUpAdminRequest.email(),
                signUpAdminRequest.password(),
                Role.ADMIN
        );

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.SIGN_UP);
    }

    @Operation(summary = "판매자 신청 목록 조회", description = "승인 대기 중인 판매자 신청 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "ADMIN 권한 없음")
    })
    @GetMapping("/seller-applications")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<List<SellerApplicationResponse>>> getPendingSellerApplications() {
        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.OK, userService.getPendingApplications());
    }

    @Operation(summary = "판매자 신청 상태 변경", description = "판매자 신청을 승인하거나 반려합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "ADMIN 권한 없음"),
            @ApiResponse(responseCode = "404", description = "신청서를 찾을 수 없음")
    })
    @PatchMapping("/seller-applications/{applicationId}/status")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<Void>> approveSellerApplication(
            @PathVariable String applicationId,
            @RequestBody ReviewRequest request
    ) {
        userService.updateSellerApplicationStatus(applicationId, request.status());
        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.OK);
    }
}
