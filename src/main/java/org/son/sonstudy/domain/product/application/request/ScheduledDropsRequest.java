package org.son.sonstudy.domain.product.application.request;

import java.time.LocalDateTime;

public record ScheduledDropsRequest(
        LocalDateTime cursorReleasedAt,
        String cursorId,
        Integer size
) {
    public ScheduledDropsRequest normalize(int defaultSize) {
        return new ScheduledDropsRequest(
                cursorReleasedAt,
                cursorId,
                size != null ? size : defaultSize
        );
    }
}
