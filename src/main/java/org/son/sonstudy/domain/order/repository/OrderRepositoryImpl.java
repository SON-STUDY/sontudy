package org.son.sonstudy.domain.order.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.domain.delivery.model.Delivery;
import org.son.sonstudy.domain.order.model.Order;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderHistoryRow> findOrderHistoryByCursor(
            String userId,
            LocalDateTime cursorOrderDate,
            String cursorOrderId,
            int size
    ) {
        PathBuilder<Order> order = new PathBuilder<>(Order.class, "order");
        PathBuilder<Product> product = new PathBuilder<>(Product.class, "product");
        PathBuilder<ProductOption> option = new PathBuilder<>(ProductOption.class, "option");
        PathBuilder<Delivery> delivery = new PathBuilder<>(Delivery.class, "delivery");
        PathBuilder<ProductImage> productImage = new PathBuilder<>(ProductImage.class, "productImage");

        return queryFactory
                .select(Projections.constructor(
                        OrderHistoryRow.class,
                        order.getString("id"),
                        order.getDateTime("orderDate", LocalDateTime.class),
                        order.getEnum("status", org.son.sonstudy.domain.order.model.OrderStatus.class),
                        product.getString("name"),
                        productImage.getString("imageUrl"),
                        option.getNumber("size", Integer.class),
                        order.getNumber("cost", Integer.class).longValue(),
                        delivery.getEnum("status", org.son.sonstudy.domain.delivery.model.DeliveryStatus.class),
                        delivery.getString("trackingNumber")
                ))
                .from(order)
                .join(order.get("product", Product.class), product)
                .join(order.get("productOption", ProductOption.class), option)
                .leftJoin(order.get("delivery", Delivery.class), delivery)
                .leftJoin(product.getList("images", ProductImage.class), productImage)
                .on(productImage.getNumber("orders", Integer.class).eq(0))
                .where(
                        order.get("user").getString("id").eq(userId),
                        buildCursorPredicate(order, cursorOrderDate, cursorOrderId)
                )
                .orderBy(
                        order.getDateTime("orderDate", LocalDateTime.class).desc(),
                        order.getString("id").desc()
                )
                .limit(size + 1L)
                .fetch();
    }

    private BooleanBuilder buildCursorPredicate(
            PathBuilder<Order> order,
            LocalDateTime cursorOrderDate,
            String cursorOrderId
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        var orderDate = order.getDateTime("orderDate", LocalDateTime.class);
        var orderId = order.getString("id");
        if (cursorOrderDate != null && cursorOrderId != null) {
            builder.and(
                    orderDate.lt(cursorOrderDate)
                            .or(orderDate.eq(cursorOrderDate)
                                    .and(orderId.lt(cursorOrderId)))
            );
        }
        return builder;
    }
}
