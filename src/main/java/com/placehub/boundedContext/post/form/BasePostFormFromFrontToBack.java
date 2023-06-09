package com.placehub.boundedContext.post.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class BasePostFormFromFrontToBack {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitedDate;
    private String content;
    private List<MultipartFile> images;
}