package com.placehub.boundedContext.member.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Viewer {
    private String username;
    private LocalDate visitedDate;
    private String content;
}
