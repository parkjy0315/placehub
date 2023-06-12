package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.post.entity.Images;
import com.placehub.boundedContext.post.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

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
        Optional<List<Images>> wrappedImages = imageRepository.findImagesByPost(postId);
        List<Images> images = new ArrayList<>();
        if (wrappedImages.isPresent()) {
            images = wrappedImages.get();
        }

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

    private long countImages(Optional<List<Images>> wrappedImgs) {
        if (wrappedImgs.isEmpty()) {
            return 0L;
        }

        return wrappedImgs.get().size();
    }

    private long maxImageId(Optional<List<Images>> wrappedImgs) {
        if (wrappedImgs.get().isEmpty()) {
            return 0L;
        }

        return wrappedImgs.get()
                .stream()
                .max(Comparator.comparing(Images::getImg))
                .get()
                .getImg();
    }

    public RsData controlImage(List<MultipartFile> files, long postId, ImageControlOptions control) {
        for (MultipartFile singleFile : files) {
            if (!singleFile.getContentType().equals("application/octet-stream")
                    && !singleFile.getContentType().startsWith("image/")) {
                return RsData.of("F-4", "이미지 파일이 아닌 것이 있습니다.");
            }
        }
        Optional<List<Images>> wrappedImgs = imageRepository.findImagesByPost(postId);
        List<Images> images = wrappedImgs.get();

        if (control == ImageControlOptions.MODIFY) {
            Set<Long> sentImgs = new HashSet<>();
            List<MultipartFile> readyToSave = new ArrayList<>();
            Set<Long> idSetFromDb = getImIdsFromDB(images);
            distinguishImages(files, sentImgs, readyToSave, idSetFromDb);

            RsData deleteResult = deleteParticially(idSetFromDb, sentImgs, images);
            if (deleteResult.isFail()) {
                return deleteResult;
            }

            long alreadySavedImgCount = idSetFromDb.size();
            long maxImgId = maxImageId(wrappedImgs);

            return saveImages(readyToSave, postId, alreadySavedImgCount, maxImgId);
        }

        long alreadySavedImgCount = images.size();
        long maxImgId = maxImageId(wrappedImgs);
        return saveImages(files, postId, alreadySavedImgCount, maxImgId);
    }

    private Set<Long> getImIdsFromDB(List<Images> images) {
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

    public RsData deleteAllInPost(long postId) {
        Optional<List<Images>> wrappedImgs = imageRepository.findImagesByPost(postId);
        if (wrappedImgs.isEmpty()) {
            return RsData.of("F-3", "존재하지 않는 게시글입니다");
        }

        List<Images> images = wrappedImgs.get();
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

    private RsData saveImages(List<MultipartFile> files, long postId, long alreadySavedImgCount, long maxImgId) {

        if (files.size() + alreadySavedImgCount > 10) {
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
