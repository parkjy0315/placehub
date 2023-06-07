package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.post.entity.Images;
import com.placehub.boundedContext.post.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Service
public class ImageService {
    private static final String fileSeperator = File.separator;
    private static final String IMAGE_STORAGE_PATH = "src" + fileSeperator + "main" + fileSeperator + "resources"
                                                            + fileSeperator +"static" + fileSeperator +"resource"
                                                            + fileSeperator + "postImages";
    @Autowired
    private ImageRepository imageRepository;

    private RsData validateAcceptedImage(List<MultipartFile> files, long savedImgsCount) {
        for (MultipartFile singleFile : files) {
            if (!singleFile.getContentType().startsWith("image/")) {
                return RsData.of("F-4", "이미지 파일이 아닌 것이 있습니다.");
            }
        }

        if (files.size() + savedImgsCount > 10) {
            return RsData.of("F-4", "이미지는 최대 10개까지만 첨부가능합니다.");
        }

        return RsData.of("S-4", "정상입니다");
    }

    private void mkImageDir() {
        File imageDir = new File(IMAGE_STORAGE_PATH);
        if (!imageDir.exists()) {
            try {
                imageDir.mkdir();
            } catch (Exception mkDirException) {
                mkDirException.getStackTrace();
            }
        }
    }

    public RsData saveImages(List<MultipartFile> files, long postId) {
        mkImageDir();

        long alreadySavedImgCount = 0L;
        Optional<List<Images>> wrappedImgs = imageRepository.findImagesByPost(postId);
        List<Images> alreadySavedImgs = new ArrayList<>();
        if (wrappedImgs.isPresent()) {
            alreadySavedImgs = wrappedImgs.get();
        }
        alreadySavedImgCount = alreadySavedImgs.size();


        RsData isValidate = validateAcceptedImage(files, alreadySavedImgCount);
        if (isValidate.isFail()) {
            return isValidate;
        }


        long maxImgId = 0;
        if (!alreadySavedImgs.isEmpty()) {
            maxImgId = alreadySavedImgs
                    .stream()
                    .max(Comparator.comparing(Images::getImg))
                    .get()
                    .getImg() + 1;
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) { continue; }

            String fileType = "." + file.getContentType().split("/")[1];
            Path filePath = Path.of(IMAGE_STORAGE_PATH + fileSeperator + postId + "_" + maxImgId + fileType);
            maxImgId++;
            Images img = Images
                    .builder()
                    .img(maxImgId)
                    .post(postId)
                    .fileType(fileType)
                    .build();

            imageRepository.save(img);

            try {
                file.transferTo(filePath);
            } catch (IOException e) {
                return RsData.of("F-5", "이미지 파일을 저장하는데 실패했습니다");
            }
        }

        return RsData.of("S-1", "이미지 저장 성공");
    }
}
