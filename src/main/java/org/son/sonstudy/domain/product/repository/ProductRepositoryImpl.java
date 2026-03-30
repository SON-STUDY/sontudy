package org.son.sonstudy.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;
import org.son.sonstudy.domain.product.dto.ProductSearchFilter;
import org.son.sonstudy.domain.product.model.*;
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

        JPAQuery<Product> query = queryFactory
                .selectFrom(product)
                .leftJoin(option).on(option.product.id.eq(product.id));

        if (userId != null) {
            query.leftJoin(productLike).on(product.id.eq(productLike.product.id)
                    .and(productLike.user.id.eq(userId)));
        }

        List<Product> content = query
                .where(
                        product.status.eq(ProductStatus.ON_SALE)
                                .or(product.status.eq(ProductStatus.END).and(option.stock.gt(0)))
                )
                .groupBy(product.id)
                .orderBy(
                        // userId가 있을 때만 likeOrder 적용, 없으면 null 처리되어 무시됨
                        userId != null ? likeOrder.asc() : null,
                        statusOrder.asc(),        // 1. (혹은 2.) ON_SALE 상태 먼저
                        option.stock.sum().asc(), // 2. (혹은 3.) 재고 적은 순
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
    public Page<Product> findProductsByFilter(ProductSearchFilter filter, Pageable pageable){
        QProduct product = QProduct.product;

        List<Product> content = queryFactory
                .selectFrom(product)
                .where(
                        brandEq(filter.brand()),
                        statusEq(filter.status())
                )
                .orderBy(product.releasedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        brandEq(filter.brand()),
                        statusEq(filter.status())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
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

    private BooleanExpression brandEq(String brand) {
        return StringUtils.hasText(brand) ? QProduct.product.brand.equalsIgnoreCase(brand) : null;
    }

    private BooleanExpression statusEq(ProductStatus status) {
        return status != null ? QProduct.product.status.eq(status) : null;
    }
}
