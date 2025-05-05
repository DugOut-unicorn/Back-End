package dugout.DugOut.service;

import dugout.DugOut.web.dto.response.NewsResponse;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NewsFetchService {

    private final RestTemplate restTemplate;

    // 모바일 뉴스 URL 포맷
    private static final String MOBILE_URL =
            "https://m.sports.naver.com/kbaseball/news?sectionId=kbaseball&sort=popular&date=%s&isPhoto=N";
    private static final String TARGET_API = "http://localhost:8080/api/news";

    public NewsFetchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void fetchAndPush() {
        String todayIso = LocalDate.now().format(DateTimeFormatter.ISO_DATE);  // yyyy-MM-dd
        List<NewsResponse> items = scrapeWithSelenium(todayIso);
        if (!items.isEmpty()) {
            Map<String, Object> body = Map.of("date", todayIso, "items", items);
            restTemplate.postForEntity(TARGET_API, body, Void.class);
        }
    }

    public List<NewsResponse> scrapeWithSelenium(String isoDate) {
        // 1) WebDriverManager로 ChromeDriver 바이너리 자동 다운로드/설정
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless","--no-sandbox","--disable-gpu","--window-size=1920,1080");
        WebDriver driver = new ChromeDriver(options);

        try {
            String yyyyMMdd = isoDate.replace("-", "");
            String url = String.format(MOBILE_URL, yyyyMMdd);
            System.out.println("[NewsFetch] Selenium GET " + url);
            driver.get(url);

            // 2) React 컴포넌트 로드 대기
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions
                            .presenceOfElementLocated(By.cssSelector("div.NewsList_comp_news_list__oXAbN")));

            // 3) 페이지 소스 가져와서 Jsoup 으로 다시 파싱
            String html = driver.getPageSource();
            Document doc = Jsoup.parse(html);

            Element container = doc.selectFirst("div.NewsList_comp_news_list__oXAbN");
            if (container == null) {
                System.err.println("[NewsFetch] 컨테이너 없음");
                return List.of();
            }

            List<NewsResponse> result = new ArrayList<>();
            for (Element li : container.select("li.NewsItem_news_item__fhEmd")) {
                String title = li.selectFirst("em.NewsItem_title__BXkJ6").text();
                String linkRel = li.selectFirst("a.NewsItem_link_news__tD7x3").attr("href");
                String link = URI.create("https://sports.news.naver.com").resolve(linkRel).toString();
                Element img = li.selectFirst("div.NewsItem_image_area__nv6V0 img");
                String imgSrc = (img != null ? img.attr("src") : "");
                result.add(new NewsResponse(title, link, imgSrc));
                if (result.size() >= 10) break;
            }
            System.out.println("[NewsFetch] Found " + result.size());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            driver.quit();
        }
    }

}


