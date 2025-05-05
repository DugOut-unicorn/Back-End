package dugout.DugOut.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class KmaClient {
    private final WebClient wc;
    private final String serviceKey;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final List<Integer> FCST_HOURS = List.of(2,5,8,11,14,17,20,23);

    public KmaClient(
            @Value("${kma.base-url}") String baseUrl,
            @Value("${kma.service.key}") String serviceKey
    ) {
        this.serviceKey = serviceKey;
        this.wc = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String pickForecastBaseTime(LocalDateTime now) {
        int hour = FCST_HOURS.stream()
                .filter(h -> h <= now.getHour())
                .max(Integer::compare)
                .orElse(23);
        return String.format("%02d00", hour);
    }

    public Mono<UltraSrtFcstAllResponse> getUltraSrtFcstAll(int nx, int ny) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String baseDate = DATE_FMT.format(now);
        String baseTime = pickForecastBaseTime(now);

        return wc.get()
                .uri(uri -> uri
                        .path("/getUltraSrtFcst")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1000)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", baseDate)
                        .queryParam("base_time", baseTime)
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build()
                )
                .exchangeToMono(response -> handleResponse(response, nx, ny));
    }

    private Mono<UltraSrtFcstAllResponse> handleResponse(ClientResponse response, int nx, int ny) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String.class)
                    .doOnNext(json -> log.info("[KMA RAW] nx={}, ny={} → {}", nx, ny, json))
                    .flatMap(json -> {
                        try {
                            UltraSrtFcstAllResponse dto =
                                    objectMapper.readValue(json, UltraSrtFcstAllResponse.class);
                            log.info("[KMA PARSED] nx={}, ny={} → items={}",
                                    nx, ny,
                                    dto.getResponse().getBody().getItems().getItem().size());
                            return Mono.just(dto);
                        } catch (Exception e) {
                            log.error("[KMA PARSE ERROR] nx={}, ny={}", nx, ny, e);
                            return Mono.error(e);
                        }
                    });
        } else {
            return response.bodyToMono(String.class)
                    .doOnNext(body -> log.error("[KMA HTTP ERROR] status={}, body={}",
                            response.statusCode(), body))
                    .flatMap(body -> Mono.error(
                            new RuntimeException("KMA HTTP " + response.statusCode())
                    ));
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class UltraSrtFcstAllResponse {
        private Response response;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data public static class Response {
            private Body body;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data public static class Body {
            private Items items;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data public static class Items {
            private List<Item> item;
        }

        @Data public static class Item {
            private String category;
            private String fcstValue;
        }
    }
}
