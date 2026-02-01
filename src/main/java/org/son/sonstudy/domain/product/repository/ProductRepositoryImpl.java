package org.son.sonstudy.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductStatus;
import org.son.sonstudy.domain.product.model.QProduct;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findScheduledDropsByCursor(LocalDateTime cursorReleasedAt, String cursorId, int size) {
        QProduct product = QProduct.product;
        BooleanBuilder cursorPredicate = buildCursorPredicate(product, cursorReleasedAt, cursorId);

        return queryFactory.selectFrom(product)
                .where(
                        product.status.eq(ProductStatus.SCHEDULED),
                        product.releasedAt.goe(LocalDateTime.now()),
                        cursorPredicate
                )
                .orderBy(product.releasedAt.asc(), product.id.asc())
                .limit(size + 1L) // size + 1개 만큼 조회해서 hasNext가 있는지 판별함
                .fetch();
    }

    private BooleanBuilder buildCursorPredicate(
            QProduct product,
            LocalDateTime cursorReleasedAt,
            String cursorId
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        if (cursorReleasedAt != null && cursorId != null) {
            builder.and(
                    product.releasedAt.gt(cursorReleasedAt)
                            .or(product.releasedAt.eq(cursorReleasedAt)
                                    .and(product.id.gt(cursorId)))
            );
        }
        return builder;
    }
}
