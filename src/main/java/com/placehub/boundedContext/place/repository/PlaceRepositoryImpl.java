package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.entity.QPlace;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPlace place = QPlace.place;

    @Override
    public List<Place> findPlaceBySpecificDistance(Point point, Long distance) {
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(
                Double.class,
                "ST_distance_sphere({0}, {1})", place.point, point);

        return jpaQueryFactory
                .selectFrom(place)
                .where(distanceExpression.loe(distance))
                .fetch();
    }

    @Override
    public Page<Place> findPlaceBySpecificDistance(Pageable pageable, Point point, Long distance) {
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(
                Double.class,
                "ST_distance_sphere({0}, {1})", place.point, point);

        JPQLQuery<Place> query = jpaQueryFactory
                .selectFrom(place)
                .where(distanceExpression.loe(distance));

        List<Place> resultList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        QueryResults<Place> queryResults = query.fetchResults();
        long total = queryResults.getTotal();

        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public Page<Place> findPlaceBySpecificDistanceAndBigId(Pageable pageable, Point point, Long distance, Long bigCategoryId) {
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class, "ST_distance_sphere({0}, {1})", place.point, point);
        BooleanExpression whereClause = distanceExpression.loe(distance)
                .and(place.bigCategoryId.eq(bigCategoryId));

        JPQLQuery<Place> query = jpaQueryFactory
                .selectFrom(place)
                .where(whereClause);

        List<Place> resultList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        QueryResults<Place> queryResults = query.fetchResults();
        long total = queryResults.getTotal();

        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public Page<Place> findPlaceBySpecificDistanceAndBigIdAndMidId(Pageable pageable, Point point, Long distance, Long bigCategoryId, Long midCategoryId) {
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class, "ST_distance_sphere({0}, {1})", place.point, point);
        BooleanExpression whereClause = distanceExpression.loe(distance)
                .and(place.bigCategoryId.eq(bigCategoryId))
                .and(place.midCategoryId.eq(midCategoryId));

        JPQLQuery<Place> query = jpaQueryFactory
                .selectFrom(place)
                .where(whereClause);

        List<Place> resultList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        QueryResults<Place> queryResults = query.fetchResults();
        long total = queryResults.getTotal();

        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public Page<Place> findPlaceBySpecificDistanceAndBigIdAndMidIdAndSmallId(Pageable pageable, Point point, Long distance, Long bigCategoryId, Long midCategoryId, Long smallCategoryId) {
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class, "ST_distance_sphere({0}, {1})", place.point, point);
        BooleanExpression whereClause = distanceExpression.loe(distance)
                .and(place.bigCategoryId.eq(bigCategoryId))
                .and(place.midCategoryId.eq(midCategoryId))
                .and(place.smallCategoryId.eq(smallCategoryId));

        JPQLQuery<Place> query = jpaQueryFactory
                .selectFrom(place)
                .where(whereClause);

        List<Place> resultList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        QueryResults<Place> queryResults = query.fetchResults();
        long total = queryResults.getTotal();

        return new PageImpl<>(resultList, pageable, total);
    }
}
