package org.son.sonstudy.domain.order.repository;

import org.son.sonstudy.domain.order.business.response.OrderHistoryResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepositoryCustom {
    List<OrderHistoryResponse.OrderHistoryItem> findOrderHistoryByCursor(
            String userId,
            LocalDateTime cursorOrderDate,
            String cursorOrderId,
            int size
    );
}
