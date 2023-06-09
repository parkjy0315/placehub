package com.placehub.boundedContext.place.eventListener;

import com.placehub.base.event.EventAfterUpdatePlaceLike;
import com.placehub.boundedContext.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceEventListener {
    private final PlaceService placeService;

    @EventListener
    public void listen(EventAfterUpdatePlaceLike event){
        placeService.whenUpdatePlaceLike(event.getPlaceId(), event.isCreated());
    }
}
