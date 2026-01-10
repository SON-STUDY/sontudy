package org.son.sonstudy.domain.user.business.response;

import org.son.sonstudy.domain.user.model.submodel.ApplicationStatus;
import org.son.sonstudy.domain.user.model.submodel.SellerApplication;

import java.time.LocalDateTime;

public record SellerApplicationResponse(
        String id,
        String userId,
        String userName,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
    public static SellerApplicationResponse from(SellerApplication application) {
        return new SellerApplicationResponse(
                application.getId(),
                application.getUser().getId(),
                application.getUser().getName(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}
