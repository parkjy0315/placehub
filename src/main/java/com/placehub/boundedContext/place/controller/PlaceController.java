package com.placehub.boundedContext.place.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.base.util.PlaceProcessor;
import com.placehub.base.util.Ut;
import com.placehub.boundedContext.category.entity.BigCategory;
import com.placehub.boundedContext.category.entity.MidCategory;
import com.placehub.boundedContext.category.entity.SmallCategory;
import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.place.dto.PlaceInfo;
import com.placehub.boundedContext.place.dto.SearchCriteria;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.service.PlaceInfoService;
import com.placehub.boundedContext.place.service.PlaceService;
import com.placehub.boundedContext.placelike.entity.PlaceLike;
import com.placehub.boundedContext.placelike.service.PlaceLikeService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.form.Viewer;
import com.placehub.boundedContext.post.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {
    private final PlaceService placeService;
    private final PlaceInfoService placeInfoService;
    private final BigCategoryService bigCategoryService;
    private final MidCategoryService midCategoryService;
    private final SmallCategoryService smallCategoryService;
    private final PlaceLikeService placeLikeService;
    private final PostService postService;
    private final PlaceProcessor placeProcessor;
    private final Rq rq;

    @Data
    public class SearchForm {
        private Double latitude;
        private Double longitude;
        private Long bigCategoryId;
        private Long midCategoryId;
    }

    @GetMapping("/search")
    public String search(
            Model model,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(defaultValue = "2000") Long distance,
            @RequestParam(value = "bigCategoryId", required = false) Long bigCategoryId,
            @RequestParam(value = "midCategoryId", required = false) Long midCategoryId,
            @RequestParam(value = "smallCategoryId", required = false) Long smallCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        // 카테고리 정보
        List<BigCategory> bigCategories = bigCategoryService.findAll();
        List<MidCategory> midCategories = midCategoryService.findAll();
        List<SmallCategory> smallCategories = smallCategoryService.findAll();

        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        model.addAttribute("bigCategories", bigCategories);
        model.addAttribute("midCategories", midCategories);
        model.addAttribute("smallCategories", smallCategories);
        model.addAttribute("selectedBig", bigCategoryId);
        model.addAttribute("selectedMid", midCategoryId);
        model.addAttribute("selectedSmall", smallCategoryId);
        // model.addAttribute("level", 5);

        // 페이징 정보
        Sort sort = Sort.by(Sort.Direction.DESC, "likeCount");
        Pageable pageable = PageRequest.of(page, size, sort);

        // 검색 결과 정보
        List<PlaceInfo> placeInfoList = null;
        Page<Place> placePage = null;

        // 좌표 유효성 검사
        RsData validRs = placeService.isValidCoordinate(longitude, latitude);

        switch (validRs.getResultCode()) {
            case "F-1": // 좌표설정 오류
            case "F-2": // 좌표범위 오류
                placePage = placeService.findAll(pageable);
                placeInfoList = placeInfoService.getCategoryNamesList(placePage.getContent());
                model.addAttribute("paging", placePage);
                model.addAttribute("placeInfoList", placeInfoList);
                model.addAttribute("latitude", 37.564989);
                model.addAttribute("longitude", 126.9771);
                // 다음과 같은 처리를 하면 url을 통한 접근 시 붕뜸
                return rq.historyBack(validRs);

            case "S-1": // 초기화면 요청
                placePage = placeService.findAll(pageable);
                placeInfoList = placeInfoService.getCategoryNamesList(placePage.getContent());
                model.addAttribute("paging", placePage);
                model.addAttribute("placeInfoList", placeInfoList);
                model.addAttribute("latitude", 37.564989);
                model.addAttribute("longitude", 126.9771);
                // model.addAttribute("level", 3);

                // 좌표정보
                double xPosAverageByPlace = placeInfoList.stream()
                        .mapToDouble(place -> place.getPlace().getPoint().getX())
                        .average()
                        .orElse(0);

                double yPosAverageByPlace = placeInfoList.stream()
                        .mapToDouble(place -> place.getPlace().getPoint().getY())
                        .average()
                        .orElse(0);

                model.addAttribute("xPosAverageByPlace", xPosAverageByPlace);
                model.addAttribute("yPosAverageByPlace", yPosAverageByPlace);

                return "usr/place/search";

            case "S-2": // 정상 좌표
                Point point = Ut.point.toPoint(longitude, latitude);
                List<Long> categoryIds = placeService.makeCategoryList(bigCategoryId, midCategoryId, smallCategoryId);
                SearchCriteria searchCriteria = new SearchCriteria(point, distance, categoryIds);

                placePage = placeService.findPlace(pageable, searchCriteria);
                break;
        }

        // 장소 정보
        placeInfoList = placeInfoService.getCategoryNamesList(placePage.getContent());
        model.addAttribute("paging", placePage);
        model.addAttribute("placeInfoList", placeInfoList);

        // 좌표정보
        double xPosAverageByPlace = placeInfoList.stream()
                .mapToDouble(place -> place.getPlace().getPoint().getX())
                .average()
                .orElse(0);

        double yPosAverageByPlace = placeInfoList.stream()
                .mapToDouble(place -> place.getPlace().getPoint().getY())
                .average()
                .orElse(0);

        model.addAttribute("xPosAverageByPlace", xPosAverageByPlace);
        model.addAttribute("yPosAverageByPlace", yPosAverageByPlace);


        // 페이징 정보
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", placePage.getTotalPages());
        model.addAttribute("totalElements", placePage.getTotalElements());

        return "usr/place/search";
    }

    @GetMapping("/details/{placeId}")
    public String details(Model model, @PathVariable("placeId") Long id,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "7") int size) {
        Place place = placeService.getPlace(id);

        if (place == null) {
            return rq.historyBack("해당 장소는 없습니다.");
        }

        model.addAttribute("place", place);

        PlaceInfo placeInfo = placeInfoService.getCategoryNames(place);
        model.addAttribute("placeInfo", placeInfo);

        if (rq.getMember() != null) {
            PlaceLike placeLike = placeLikeService.findByPlaceIdAndMemberId(id, rq.getMember().getId());
            model.addAttribute("placeLike", placeLike);
        }


        // 아카이빙 포스트 정보
        Sort sort = Sort.by(Sort.Direction.DESC, "visitedDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> postPage = postService.findByPlace(id, pageable);
        List<Viewer> postViewerList = new ArrayList<>();
        for (Post post : postPage) {
            postViewerList.add(postService.showSinglePost(post.getId()).getData());
        }

        model.addAttribute("paging", postPage);
        model.addAttribute("postList", postViewerList);

        return "usr/place/details";
    }

    @GetMapping("/data-save-test/{categoryName}")
    @ResponseBody
    public String saveData(@PathVariable("categoryName") String categoryName) {
        Map<String, String> categoryCode = new HashMap<>() {{
            put("문화시설", "CT1"); // 2522
            put("관광명소", "AT4"); // 1111
            put("음식점", "FD6"); // 135651
            put("카페", "CE7"); // 33501
        }};

        placeProcessor.saveAllCategoryData(categoryCode.get(categoryName));
        return "Success";
    }
}
