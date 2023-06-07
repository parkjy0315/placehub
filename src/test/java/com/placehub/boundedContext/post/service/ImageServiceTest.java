package com.placehub.boundedContext.post.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class ImageServiceTest {
    @Autowired
    ImageService imageService;
    @Test
    void imageSaveTest() {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        assertThat(imageService.saveImages(multipartFiles, 3L).isFail()).isTrue();
    }
}
