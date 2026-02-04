package org.son.sonstudy.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.domain.product.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

    @Override
    public Slice<Product> findLiveDrops(String userId, Pageable pageable) {
        QProduct product = QProduct.product;
        QProductOption option = QProductOption.productOption;
        QProductLike productLike = QProductLike.productLike;

        // (좋아요=0, 아니면=1)
        NumberExpression<Integer> likeOrder = new CaseBuilder()
                .when(productLike.id.isNotNull()).then(0)
                .otherwise(1);

        // (ON_SALE=0, END=1)
        NumberExpression<Integer> statusOrder = new CaseBuilder()
                .when(product.status.eq(ProductStatus.ON_SALE)).then(0)
                .otherwise(1);

        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.options, option)
                .leftJoin(productLike).on(product.id.eq(productLike.product.id)
                        .and(productLike.user.id.eq(userId)))
                .where(
                        product.status.eq(ProductStatus.ON_SALE)
                                .or(product.status.eq(ProductStatus.END).and(option.stock.gt(0)))
                )
                .groupBy(product.id)
                .orderBy(
                        likeOrder.asc(),          // 1. 좋아요 먼저
                        statusOrder.asc(),        // 2. ON_SALE 상태 먼저
                        option.stock.sum().asc(), // 3. 재고 적은 순
                        product.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Product> findLiveDropsWithoutUser(Pageable pageable) {
        QProduct product = QProduct.product;
        QProductOption option = QProductOption.productOption;

        NumberExpression<Integer> statusOrder = new CaseBuilder()
                .when(product.status.eq(ProductStatus.ON_SALE)).then(0)
                .otherwise(1);

        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.options, option)
                .where(
                        product.status.eq(ProductStatus.ON_SALE)
                                .or(product.status.eq(ProductStatus.END).and(option.stock.gt(0)))
                )
                .groupBy(product.id)
                .orderBy(
                        statusOrder.asc(),
                        option.stock.sum().asc(),
                        product.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
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
