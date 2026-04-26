package org.son.sonstudy.domain.delivery.business.tracker;

public interface DeliveryTracker {
    DeliveryTrackingResult track(String courierCode, String trackingNumber);
}
