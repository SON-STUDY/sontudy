package org.son.sonstudy.domain.order.application.request;

import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record OrderHistoryRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime cursorOrderDate,
        String cursorOrderId,
        @Positive Integer size
) {
    private static final int DEFAULT_SIZE = 10;

    public int sizeOrDefault() {
        return size == null ? DEFAULT_SIZE : size;
    }
}
