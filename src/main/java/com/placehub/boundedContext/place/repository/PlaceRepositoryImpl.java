package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.entity.QPlace;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;

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
}
