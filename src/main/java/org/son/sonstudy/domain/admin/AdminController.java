package org.son.sonstudy.domain.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.common.api.response.ApiResponse;
import org.son.sonstudy.domain.user.business.UserService;
import org.son.sonstudy.domain.user.business.response.SellerApplicationResponse;
import org.son.sonstudy.domain.user.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> signUpForAdmin(
            @Valid @RequestBody SignUpAdminRequest signUpAdminRequest
    ) {
        userService.signUp(
                signUpAdminRequest.name(),
                signUpAdminRequest.email(),
                signUpAdminRequest.password(),
                Role.ADMIN
        );

        return ApiResponse.success(SuccessCode.SIGN_UP);
    }

    @GetMapping("/seller-applications")
    public ResponseEntity<ApiResponse<List<SellerApplicationResponse>>> getPendingSellerApplications() {
        return ApiResponse.success(SuccessCode.OK, userService.getPendingApplications());
    }

    @PatchMapping("/seller-applications/{applicationId}/status")
    public ResponseEntity<ApiResponse<Void>> approveSellerApplication(
            @PathVariable String applicationId,
            @RequestBody ReviewRequest request
    ) {
        userService.updateSellerApplicationStatus(applicationId, request.status());
        return ApiResponse.success(SuccessCode.OK);
    }
}
