package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class ImageService {
    private static final String fileSeperator = File.separator;
    private static final String IMAGE_STORAGE_PATH = "src" + fileSeperator + "main" + fileSeperator + "resources"
                                                            + fileSeperator +"static" + fileSeperator +"resource"
                                                            + fileSeperator + "postImages" + fileSeperator;

    private RsData validateAcceptedImage(MultipartFile singleFile) {
        if (!singleFile.getContentType().startsWith("image/")) {
            return RsData.of("F-4", "이미지 파일이 아닙니다");
        }

        return RsData.of("S-4", "이미지 파일이 맞습니다");
    }

    public RsData saveImages(List<MultipartFile> files, long postId) {
        for (MultipartFile file : files) {
            RsData fileValidation = validateAcceptedImage(file);
            if (fileValidation.isFail()) {
                return fileValidation;
            }
        }

        long fileNumber = 0;
        for (MultipartFile file : files) {
            if (file.isEmpty()) { continue; }

            String fileType = "." + file.getContentType().split("/")[1];
            Path filePath = Path.of(IMAGE_STORAGE_PATH + postId + "_" + fileNumber + fileType);
            try {
                file.transferTo(filePath);
            } catch (IOException e) {
                return RsData.of("F-5", "이미지 파일을 저장하는데 실패했습니다");
            }
        }

        return RsData.of("S-1", "이미지 저장 성공");
    }
}
