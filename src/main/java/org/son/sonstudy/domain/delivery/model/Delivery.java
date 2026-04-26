package org.son.sonstudy.domain.delivery.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;

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

    public static Delivery createReady() {
        Delivery delivery = new Delivery();
        delivery.status = DeliveryStatus.READY;
        return delivery;
    }

    public void registerShipment(String courierCompany, String trackingNumber) {
        if (this.status != DeliveryStatus.READY) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_STATUS);
        }
        this.courierCompany = courierCompany;
        this.trackingNumber = trackingNumber;
        this.status = DeliveryStatus.DELIVERING;
    }

    public void complete() {
        if (this.status != DeliveryStatus.DELIVERING) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_STATUS);
        }
        this.status = DeliveryStatus.DELIVERED;
    }
}
