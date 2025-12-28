package org.son.sonstudy.domain.delivery.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {
    @Id
    @Tsid
    private String id;

    // 택배사 정보
    private String courierCompany;

    // 운송장 번호
    private String trackingNumber;

    private String destination;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}
