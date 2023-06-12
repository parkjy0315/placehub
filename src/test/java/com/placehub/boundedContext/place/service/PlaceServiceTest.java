package com.placehub.boundedContext.place.service;

import com.placehub.boundedContext.place.entity.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class PlaceServiceTest {
    @Autowired
    private PlaceService placeService;

    @Test
    @DisplayName("PlaceService CRUD Test")
    public void t001() {
        String placeName = "임시 장소1";
        Double xPos = 0.1;
        Double yPos = 0.2;
        String category = "임시 카테고리1";

//        // CREATE
//        Place temp0 = placeService.create(placeName, xPos, yPos, category);
//        assertThat(temp0.getPlaceName()).isEqualTo(placeName);
//        assertThat(temp0.getXPos()).isEqualTo(xPos);
//        assertThat(temp0.getYPos()).isEqualTo(yPos);
//        assertThat(temp0.getCategory()).isEqualTo(category);
//
//        // READ
//        Long id = temp0.getId();
//        Place temp1 = placeService.read(id);
//        assertThat(temp1.getPlaceName()).isEqualTo(placeName);
//        assertThat(temp1.getXPos()).isEqualTo(xPos);
//        assertThat(temp1.getYPos()).isEqualTo(yPos);
//        assertThat(temp1.getCategory()).isEqualTo(category);
//
//        // DELETE
//        placeService.delete(temp1);
//        Place temp2 = placeService.read(id);
//        assertThat(temp2).isEqualTo(null);
//
//        String updateName = "임시 장소2";
//        // UPDATE
//        Place temp3 = placeService.create(placeName, xPos, yPos, category);
//        Place temp4 = placeService.update(temp3, updateName, xPos, yPos, category);
//        Place temp5 = placeService.read(temp4.getId());
//        assertThat(temp5.getPlaceName()).isEqualTo(updateName);
    }

}
