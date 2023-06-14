package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.post.entity.Images;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.form.PreSignedUrlRequestForm;
import com.placehub.boundedContext.post.form.PreSignedUrlResponseForm;
import com.placehub.boundedContext.post.repository.ImageRepository;
import com.placehub.boundedContext.post.repository.PostRepository;
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
    private final String rootAddress = "https://placehub-images.s3.ap-northeast-2.amazonaws.com/";
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private S3Pre_signedUrl s3PreSignedUrl;
    @Value("${cloud.aws.bucket}")
    public String bucket;


    public List<String> callImagePathes(long postId) {
        List<Images> images = imageRepository.findImagesByPost(postId);
        List<String> result = new ArrayList<>();

        for (Images image : images) {
            StringBuffer imagePath = new StringBuffer();
            imagePath.append(rootAddress);
            imagePath.append(image.getPost());
            imagePath.append("_");
            imagePath.append(image.getImg());
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

    private RsData<List<Integer>> filterBeforePreSignedUrl(List<PreSignedUrlRequestForm> inputImgNames, List<Images> imagesFromDb) {
        for (PreSignedUrlRequestForm singleData : inputImgNames) {
            PreSignedUrlRequestForm.FileData fileData = singleData.getFileData();

            String type = fileData.getContentType();
            if (!type.startsWith("image/") && !type.equals("application/octet-stream")) {
                return RsData.of("F-4", "이미지 파일이 아닌 것이 있습니다.");
            }
        }

        List<Integer> validNames = new ArrayList<>();
        Set<Long> imgIdFromDb = getImgIdsFromDB(imagesFromDb);
        for (PreSignedUrlRequestForm singleData : inputImgNames) {
            PreSignedUrlRequestForm.FileData fileData = singleData.getFileData();

            String fileName = fileData.getFileName();

            String beforeDot = fileName.split("\\.")[0];
            if (!beforeDot.matches("^[0-9]+_[0-9]+$")) {
                validNames.add(singleData.getId());
                continue;
            }
            String[] pureFileName = beforeDot.split("_");
            long imgNum = Long.parseLong(pureFileName[pureFileName.length - 1]);

            if (!imgIdFromDb.contains(imgNum)) {
                validNames.add(singleData.getId());
                continue;
            }

        }

        return RsData.of("S-3", "PreSigned 필터링 이상 무", validNames);
    }

    private long getPostId(Optional<Post> wrappedMaxPostId, long postId) {
        if (postId != 0L) {
            return postId;
        }

        if (wrappedMaxPostId.isPresent()) {
            postId = wrappedMaxPostId.get().getId();
        }

        postId++;
        return postId;
    }

    public List<PreSignedUrlResponseForm>  getPreSignedUrlFromFilteredData(List<PreSignedUrlRequestForm> inputImgNames
                                            , long creating_modifying_flag) {
        Optional<Post> wrappedMaxPostId = postRepository.findFirstByOrderByIdDesc();

        long postId = getPostId(wrappedMaxPostId, creating_modifying_flag);

        List<PreSignedUrlResponseForm> result = new ArrayList<>();

        List<Images> imagesFromDb = imageRepository.findImagesByPost(postId);
        RsData<List<Integer>> filtered = filterBeforePreSignedUrl(inputImgNames, imagesFromDb);
        if (filtered.isFail()) {
            throw new RuntimeException("올바르지 않은 파일");
        }

        long maxImgId = maxImageId(imagesFromDb);
        for (int imgIdx : filtered.getData()) {
            maxImgId++;
            String fileName = postId + "_" + maxImgId;
            String preSignedUrl = s3PreSignedUrl.getPreSignedUrl(bucket, "", fileName);

            PreSignedUrlResponseForm preSignedUrlResponseForm = new PreSignedUrlResponseForm();
            preSignedUrlResponseForm.setIdx(imgIdx);
            preSignedUrlResponseForm.setFileName(maxImgId);
            preSignedUrlResponseForm.setPreSignedUrl(preSignedUrl);
            result.add(preSignedUrlResponseForm);
        }

        return result;
    }

//    @Transactional
//    public RsData<List<Images>> controlImage(List<Long> files, long postId, ImageControlOptions control) {
//        List<Images> images = imageRepository.findImagesByPost(postId);
//        long maxImgId = maxImageId(images);
//
//        if (control == ImageControlOptions.MODIFY) {
//            return modifyPost(files, postId, maxImgId, images);
//        }
//
//        if (control == ImageControlOptions.CREATE) {
//            long alreadySavedImages = images.size();
//            return saveImages(files, postId, alreadySavedImages, maxImgId);
//        }
//
//        return RsData.of("F-1", "이미지 저장 실패");
//    }


    @Transactional
    public RsData createOrModifyImages(List<Long> sentImgs, long postId) {
        List<Images> images = imageRepository.findImagesByPost(postId);
        Set<Long> idSetFromDb = getImgIdsFromDB(images);

        RsData<Integer> deleteResult = deleteParticially(sentImgs, images);
        if (deleteResult.isFail()) {
            return deleteResult;
        }

        List<Long> willBeAddedImgs = new ArrayList<>();
        for (long inputImgs : sentImgs) {
            if (!idSetFromDb.contains(inputImgs)) {
                willBeAddedImgs.add(inputImgs);
            }
        }

        if (images.size() + willBeAddedImgs.size() - deleteResult.getData() > 10) {
            return RsData.of("F-5", "이미지 첨부는 최대 10개까지 가능합니다.");
        }

        return saveImages(willBeAddedImgs, postId);
    }

    private RsData<Integer> deleteParticially (List<Long> sentImgs, List<Images> images) {
        Set<Long> idSetFromDb = getImgIdsFromDB(images);

        Set<Long> sentImgIds = new HashSet<>();
        if (sentImgs != null) {
            sentImgIds = new HashSet<>(sentImgs);
        }

        idSetFromDb.removeAll(sentImgIds);

        for (Images image : images) {
            if (!idSetFromDb.contains(image.getImg())) {
                continue;
            }

            Images deleted = image.toBuilder()
                    .deleteDate(LocalDateTime.now())
                    .build();

            imageRepository.save(deleted);
        }

        return RsData.of("S-1", "삭제 성공", idSetFromDb.size());
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

//    private void distinguishImages(List<String> inputFiles, Set<Long> sentImgs
//                                    , List<MultipartFile> readyToSave, Set<Long> idSetFromDb) {
//        for (MultipartFile multipartFile : inputFiles) {
//            String fileName = multipartFile.getOriginalFilename();
//
//            if (!validateFileNameFromClient(fileName)) {
//                readyToSave.add(multipartFile);
//                continue;
//            }
//
//            String beforeDot = fileName.split("\\.")[0];
//            long imgNum = Long.parseLong(beforeDot.split("_")[1]);
//
//            if (!idSetFromDb.contains(imgNum)) {
//                readyToSave.add(multipartFile);
//                continue;
//            }
//
//            sentImgs.add(imgNum);
//        }
//    }

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

    private RsData saveImages(List<Long> generatingImgs, long postId) {
        for (long imgId : generatingImgs) {
            Images img = Images
                    .builder()
                    .img(imgId)
                    .post(postId)
                    .build();

            imageRepository.save(img);
        }

        return RsData.of("S-1", "이미지 저장 성공");
    }
}
