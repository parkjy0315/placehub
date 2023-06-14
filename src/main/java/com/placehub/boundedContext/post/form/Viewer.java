package com.placehub.boundedContext.post.form;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class Viewer {
    private long userId;
    private String username;
    private long placeId;
    private String placeName;
    private long postId;
    private LocalDateTime createDate;
    private LocalDate visitedDate;
    private String content;
    private boolean isOpenToPublic;
    private String mainImage;

}