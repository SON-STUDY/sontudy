package org.son.sonstudy.domain.delivery.business.tracker;

import org.springframework.stereotype.Component;

@Component
public class FakeDeliveryTracker implements DeliveryTracker {

    @Override
    public DeliveryTrackingResult track(String courierCode, String trackingNumber) {
        if (trackingNumber == null) {
            return new DeliveryTrackingResult(-99, false, null, "송장번호 없음");
        }

        char lastChar = trackingNumber.charAt(trackingNumber.length() - 1);

        return switch (lastChar) {
            case '1' -> new DeliveryTrackingResult(1, false, "발송지", "배송 준비 중");
            case '2' -> new DeliveryTrackingResult(2, false, "집화지점", "집화 완료");
            case '3' -> new DeliveryTrackingResult(3, false, "옥천HUB", "배송 중");
            case '4' -> new DeliveryTrackingResult(4, false, "강남지점", "지점 도착");
            case '5' -> new DeliveryTrackingResult(5, false, "배송지 인근", "배송 출고");
            case '6' -> new DeliveryTrackingResult(6, true, "수령인 자택", "배송 완료");
            case '0' -> new DeliveryTrackingResult(-99, false, null, "스캔 오류");
            default -> new DeliveryTrackingResult(3, false, "옥천HUB", "배송 중");
        };
    }
}
