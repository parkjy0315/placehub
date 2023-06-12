package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.post.entity.Images;
import com.placehub.boundedContext.post.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Service
public class ImageService {
    private static final String fileSeperator = File.separator;
    @Value("${custom.genFileDirPath}")
    private String IMAGE_STORAGE_PATH;
    private final String rootAddress = "https://localhost:8080/";
    @Autowired
    private ImageRepository imageRepository;
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

    public List<String> callImagePathes(long postId) {
        List<Images> images = imageRepository.findImagesByPost(postId);
        List<String> result = new ArrayList<>();

        for (Images image : images) {
            StringBuffer imagePath = new StringBuffer();
            imagePath.append(rootAddress);
            imagePath.append("postImages/");
            imagePath.append(image.getPost());
            imagePath.append("_");
            imagePath.append(image.getImg());
            imagePath.append(image.getFileType());
            result.add(imagePath.toString());
        }

        return result;
    }

    private long maxImageId(List<Images> imagesList) {
        if (imagesList.isEmpty()) {
            return 0L;
        }
        return imagesList
                .stream()
                .max(Comparator.comparing(Images::getImg))
                .get()
                .getImg();
    }

    @Transactional
    public RsData<List<Images>> controlImage(List<MultipartFile> files, long postId, ImageControlOptions control) {
        for (MultipartFile singleFile : files) {
            if (!singleFile.getContentType().equals("application/octet-stream")
                    && !singleFile.getContentType().startsWith("image/")) {
                return RsData.of("F-4", "이미지 파일이 아닌 것이 있습니다.");
            }
        }
        List<Images> images = imageRepository.findImagesByPost(postId);
        long maxImgId = maxImageId(images);

        if (control == ImageControlOptions.MODIFY) {
            return modifyPost(files, postId, maxImgId, images);
        }

        if (control == ImageControlOptions.CREATE) {
            long alreadySavedImages = images.size();
            return saveImages(files, postId, alreadySavedImages, maxImgId);
        }

        return RsData.of("F-1", "이미지 저장 실패");
    }

    private RsData modifyPost(List<MultipartFile> files, long postId, long maxImgId, List<Images> images) {
        Set<Long> sentImgs = new HashSet<>();
        List<MultipartFile> readyToSave = new ArrayList<>();
        Set<Long> idSetFromDb = getImgIdsFromDB(images);
        distinguishImages(files, sentImgs, readyToSave, idSetFromDb);

        RsData deleteResult = deleteParticially(idSetFromDb, sentImgs, images);
        if (deleteResult.isFail()) {
            return deleteResult;
        }

        long alreadySavedImgCount = idSetFromDb.size();

        return saveImages(readyToSave, postId, alreadySavedImgCount, maxImgId);
    }

    private Set<Long> getImgIdsFromDB(List<Images> images) {
        Set<Long> result = new HashSet<>();
        for (Images image : images) {
            result.add(image.getImg());
        }

        return result;
    }
    
    private boolean validateFileNameFromClient(String multiPartFileName) {
        if (!multiPartFileName.contains(".")) {
            return false;
        }
        
        String beforeDot = multiPartFileName.split("\\.")[0];
        if (!beforeDot.matches("^[0-9]+_[0-9]+$")) {
            return false;
        }
        
        return true;
    }

    private void distinguishImages(List<MultipartFile> inputFiles, Set<Long> sentImgs
                                    , List<MultipartFile> readyToSave, Set<Long> idSetFromDb) {
        for (MultipartFile multipartFile : inputFiles) {
            String fileName = multipartFile.getOriginalFilename();

            if (!validateFileNameFromClient(fileName)) {
                readyToSave.add(multipartFile);
                continue;
            }

            String beforeDot = fileName.split("\\.")[0];
            long imgNum = Long.parseLong(beforeDot.split("_")[1]);

            if (!idSetFromDb.contains(imgNum)) {
                readyToSave.add(multipartFile);
                continue;
            }

            sentImgs.add(imgNum);
        }
    }

    @Transactional
    public RsData deleteAllInPost(long postId) {
        List<Images> images = imageRepository.findImagesByPost(postId);

        for (Images image : images) {
            Images deleted = image.toBuilder()
                    .deleteDate(LocalDateTime.now())
                    .build();

            imageRepository.save(deleted);
        }

        return RsData.of("S-1", "포스트 내 이미지 삭제 성공");
    }

    private RsData deleteParticially (Set<Long> storedImages, Set<Long> sentImages, List<Images> images) {
        storedImages.removeAll(sentImages);
        for (Images image : images) {
            if (!storedImages.contains(image.getImg())) {
                continue;
            }

            Images deleted = image.toBuilder()
                    .deleteDate(LocalDateTime.now())
                    .build();

            imageRepository.save(deleted);
        }

        return RsData.of("S-1", "삭제 성공");
    }

    private RsData saveImages(List<MultipartFile> files, long postId, long alreadySavedImages, long maxImgId) {

        if (files.size() + alreadySavedImages > 10) {
            return RsData.of("F-4", "이미지는 최대 10개까지만 첨부가능합니다.");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) { continue; }

            String fileType = "." + file.getContentType().split("/")[1];

            maxImgId++;
            Path filePath = Path.of(IMAGE_STORAGE_PATH + fileSeperator + postId + "_" + maxImgId + fileType);
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
