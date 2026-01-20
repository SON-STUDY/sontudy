package org.son.sonstudy.domain.user.repository;

import org.son.sonstudy.domain.user.model.submodel.ApplicationStatus;
import org.son.sonstudy.domain.user.model.submodel.SellerApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, String> {
    List<SellerApplication> findAllByStatus(ApplicationStatus status);

    boolean existsByUserIdAndStatus(String userId, ApplicationStatus status);
}
