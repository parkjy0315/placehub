package com.placehub.boundedContext.post.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Viewer {
    private String username;
    private LocalDate visitedDate;
    private String content;
    private long postId;
    private String placeName;
    private boolean isOpenToPublic;


    private long member;
    private long place;
}