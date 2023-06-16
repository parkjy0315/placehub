package com.placehub.boundedContext.post.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class BasePostFormFromFrontToBack {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitedDate;
    private String content;
    private String imgIds;
    @NotBlank
    private String isOpenToPublic;
}