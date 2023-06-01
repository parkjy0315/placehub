package com.placehub.base.util;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class LocalApi {
    private static String REST_API_KEY;
    private static String API_SERVER_HOST;
    private static String KEYWORD_URL;
    private static String CATEGORY_URL;

    @Autowired
    public LocalApi(Environment environment) {
        REST_API_KEY = environment.getProperty("custom.api.key");
        API_SERVER_HOST = environment.getProperty("custom.api.baseUrl");
        KEYWORD_URL = environment.getProperty("custom.api.keyWord");
        CATEGORY_URL = environment.getProperty("custom.api.category");
    }

    public static class KeyWord {
        /**
         * 키워드로 장소 검색하기
         * @param x - 중심좌표의 x좌표 혹은 경도값
         * @param y - 중심좌표의 y좌표 혹은 위도값
         * @param keyWord 키워드 - 검색을 원하는 질의어
         * @param radius 반경 - 중심좌표부터의 반경거리
         * @param page 결과 페이지 번호 - (최소:1, 최대:45, 기본값:1)
         * @param size 한 페이지에 보여질 문서의 개수 - (최소:1, 최대:15, 기본값:15)
         * @return JSONObejct
         */
        public static JSONObject getAll(double x, double y, String keyWord, int radius, int page, int size) {
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(API_SERVER_HOST + KEYWORD_URL)
                    .queryParam("query", keyWord)
                    .queryParam("y", x)
                    .queryParam("x", y)
                    .queryParam("radius", radius)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .encode(StandardCharsets.UTF_8)
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.add("Authorization", "KakaoAK " + REST_API_KEY);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

            RequestEntity<String> rq = new RequestEntity<>(headers, HttpMethod.GET, uri.toUri());
            ResponseEntity<String> re = restTemplate.exchange(rq, String.class);

            JSONParser parser = new JSONParser();
            JSONObject object = null;
            try {
                object = (JSONObject) parser.parse(re.getBody());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            return object;
        }
    }

    public static class Category {
        public static JSONObject getAll(double x, double y, String category, int radius, int page, int size) {
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(API_SERVER_HOST + CATEGORY_URL)
                    .queryParam("category_group_code", category)
                    .queryParam("y", x)
                    .queryParam("x", y)
                    .queryParam("radius", radius)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .encode(StandardCharsets.UTF_8)
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.add("Authorization", "KakaoAK " + REST_API_KEY);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

            RequestEntity<String> rq = new RequestEntity<>(headers, HttpMethod.GET, uri.toUri());
            ResponseEntity<String> re = restTemplate.exchange(rq, String.class);

            JSONParser parser = new JSONParser();
            JSONObject object = null;
            try {
                object = (JSONObject) parser.parse(re.getBody());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            return object;
        }

        public static JSONObject getAllRect(String rect, String category, int page, int size) {
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(API_SERVER_HOST + CATEGORY_URL)
                    .queryParam("category_group_code", category)
                    .queryParam("rect", rect)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .encode(StandardCharsets.UTF_8)
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.add("Authorization", "KakaoAK " + REST_API_KEY);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

            RequestEntity<String> rq = new RequestEntity<>(headers, HttpMethod.GET, uri.toUri());
            ResponseEntity<String> re = restTemplate.exchange(rq, String.class);

            JSONParser parser = new JSONParser();
            JSONObject object = null;
            try {
                object = (JSONObject) parser.parse(re.getBody());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            return object;
        }
    }
}
