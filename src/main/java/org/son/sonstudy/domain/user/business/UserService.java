package org.son.sonstudy.domain.user.business;

import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.jwt.data.TokenInfo;
import org.son.sonstudy.domain.auth.business.LocalAuthService;
import org.son.sonstudy.domain.user.business.response.SellerApplicationResponse;
import org.son.sonstudy.domain.user.business.response.SignUpResponse;
import org.son.sonstudy.domain.user.business.response.UserInfoResponse;
import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;
import org.son.sonstudy.domain.user.model.submodel.ApplicationStatus;
import org.son.sonstudy.domain.user.model.submodel.SellerApplication;
import org.son.sonstudy.domain.user.repository.SellerApplicationRepository;
import org.son.sonstudy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SellerApplicationRepository sellerApplicationRepository;

    private final LocalAuthService localAuthService;

    @Transactional
    public void signUp(String name, String email, String rawPassword, Role role) {

        User savedUser = localAuthService.signUp(name, email, rawPassword, role);
        userRepository.save(savedUser);
    }

    @Transactional
    public SignUpResponse login(String email, String rawPassword) {
        TokenInfo tokenInfo = localAuthService.login(email, rawPassword);

        return SignUpResponse.from(tokenInfo);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponse.of(user);
    }

    @Transactional
    public void applyForSeller(String userId) {
        if (sellerApplicationRepository.existsByUserIdAndStatus(userId, ApplicationStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_APPLIED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.getRole().equals(Role.USER)) {
            throw new CustomException(ErrorCode.ALREADY_SELLER_AUTHORIZED);
        }

        SellerApplication application = SellerApplication.builder()
                .user(user)
                .build();

        sellerApplicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<SellerApplicationResponse> getPendingApplications() {
        return sellerApplicationRepository.findAllByStatus(ApplicationStatus.PENDING).stream()
                .map(SellerApplicationResponse::from)
                .toList();
    }

    @Transactional
    public void updateSellerApplicationStatus(String applicationId, ApplicationStatus status) {
        SellerApplication application = sellerApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getStatus().equals(ApplicationStatus.PENDING)) {
            return;
        }

        application.updateStatus(status);
        application.getUser().approveSeller();
    }
}
