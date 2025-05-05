package dugout.DugOut.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dugout.DugOut.web.dto.response.NewsResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsFetchService {

    private final RestTemplate rest;

    public NewsFetchService(RestTemplate rest) {
        this.rest = rest;
    }

    /**
     * @param isoDate yyyy-MM-dd
     * @return 최신 뉴스 최대 10건
     */
    public List<NewsResponse> scrapeLatest(String isoDate) throws IOException {
        // yyyy-MM-dd → yyyyMMdd
        String ymd = isoDate.replace("-", "");

        // 1) 요청 URL
        String url = "https://api-gw.sports.naver.com/news/articles/kbaseball"
                + "?sort=popular&date=" + ymd
                + "&page=1&pageSize=50&isPhoto=N";

        // 2) 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.REFERER,
                "https://m.sports.naver.com/kbaseball/news?sectionId=kbaseball&sort=popular"
                        + "&date=" + ymd + "&isPhoto=N");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 3) API 호출
        ResponseEntity<String> resp = rest.exchange(url, HttpMethod.GET, entity, String.class);
        String json = resp.getBody();

        // 4) JSON 파싱
        ObjectMapper om = new ObjectMapper();
        JsonNode root   = om.readTree(json);
        JsonNode items  = root.path("result").path("newsList");

        List<NewsResponse> list = new ArrayList<>();
        for (JsonNode it : items) {
            // 제목
            String title = it.path("title").asText();

            // oid, aid 로 기사 URL 조합
            String oid = it.path("oid").asText();
            String aid = it.path("aid").asText();
            String link = String.format(
                    "https://sports.news.naver.com/kbaseball/article/%s/%s",
                    oid, aid
            );

            // 이미지 (큰 이미지)
            String img = it.path("image").asText("");

            list.add(new NewsResponse(title, link, img));
            if (list.size() >= 10) break;
        }

        return list;
    }
}
