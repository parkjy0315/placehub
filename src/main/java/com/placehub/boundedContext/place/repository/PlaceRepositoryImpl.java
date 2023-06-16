package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.dto.SearchCriteria;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.entity.QPlace;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPlace place = QPlace.place;

    @Override
    public List<Place> findPlaceByDistance(Point point, Long distance) {
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(
                Double.class,
                "ST_distance_sphere({0}, {1})", place.point, point);

        return jpaQueryFactory
                .selectFrom(place)
                .where(distanceExpression.loe(distance))
                .fetch();
    }

    @Override
    public Page<Place> findPlaceByDistance(Pageable pageable, Point point, Long distance) {
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(
                Double.class,
                "ST_distance_sphere({0}, {1})", place.point, point);

        JPQLQuery<Place> query = jpaQueryFactory
                .selectFrom(place)
                .where(distanceExpression.loe(distance))
                .orderBy(place.likeCount.desc());

        List<Place> resultList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        QueryResults<Place> queryResults = query.fetchResults();
        long total = queryResults.getTotal();

        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public Page<Place> findPlaceByDistanceAndIds(Pageable pageable, SearchCriteria searchCriteria) {
        NumberExpression<Double> distanceExpression = Expressions
                .numberTemplate(
                        Double.class,
                        "ST_distance_sphere({0}, {1})",
                        place.point,
                        searchCriteria.getPoint());

        BooleanExpression whereClause = distanceExpression.loe(searchCriteria.getDistance());
        List<Long> categoryIds = searchCriteria.getCategoryIds();

        switch (categoryIds.size()) {
            case 3:
                whereClause = whereClause.and(place.smallCategoryId.eq(categoryIds.get(2)));
            case 2:
                whereClause = whereClause.and(place.midCategoryId.eq(categoryIds.get(1)));
            case 1:
                whereClause = whereClause.and(place.bigCategoryId.eq(categoryIds.get(0)));
                break;
        }

        JPQLQuery<Place> query = jpaQueryFactory
                .selectFrom(place)
                .where(whereClause)
                .orderBy(distanceExpression.asc())
                .orderBy(place.likeCount.desc());

        List<Place> resultList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(resultList, pageable, query.fetchResults().getTotal());
    }

    @Override
    public Page<Place> findPlaceWithPositiveLikeCount(Pageable pageable, Long likeCountCriteria) {
        BooleanExpression likeCountCondition = place.likeCount.gt(likeCountCriteria);

        JPQLQuery<Place> query = jpaQueryFactory
                .selectFrom(place)
                .where(likeCountCondition)
                .orderBy(place.likeCount.desc());

        List<Place> resultList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(resultList, pageable, query.fetchResults().getTotal());
    }
}
