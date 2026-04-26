package org.son.sonstudy.domain.delivery.business.tracker;

public record DeliveryTrackingResult(
        int level,
        boolean completed,
        String currentLocation,
        String description
) {
    // level: 1=준비, 2=집화, 3=이동중, 4=지점도착, 5=배송출고, 6=완료, -99=오류
}
