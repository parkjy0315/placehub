package com.placehub.base.event;

import com.placehub.boundedContext.placelike.entity.PlaceLike;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterUpdatePlaceLike extends ApplicationEvent {
    private final Long placeId;
    private final boolean isCreated;

    public EventAfterUpdatePlaceLike(Object source, Long placeId, boolean isCreated){
        super(source);
        this.placeId = placeId;
        this.isCreated = isCreated;
    }

}
