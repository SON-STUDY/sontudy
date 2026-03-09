package org.son.sonstudy.domain.payment.repository;

import org.son.sonstudy.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByMerchantUid(String merchantUid);
}
