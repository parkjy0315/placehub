package com.placehub.boundedContext.post.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class BasePostFormFromFrontToBack {
    private String place;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitedDate;
    private String content;
}
