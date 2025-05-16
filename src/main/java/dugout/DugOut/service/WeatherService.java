package dugout.DugOut.service;

import dugout.DugOut.client.KmaClient;
import dugout.DugOut.domain.enums.Stadium;
import dugout.DugOut.web.dto.StadiumWeatherDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.function.Function;

@Slf4j
@Service
public class WeatherService {
    private final KmaClient kma;

    public WeatherService(KmaClient kma) {
        this.kma = kma;
    }

    public Flux<StadiumWeatherDto> getAllStadiumWeathers() {
        return Flux.fromIterable(Arrays.asList(Stadium.values()))
                .flatMap(stadium ->
                        kma.getUltraSrtFcstAll(stadium.getNx(), stadium.getNy())
                                .map(resp -> parseAll(resp, stadium))
                                .doOnError(e ->
                                        log.error("[WEATHER ERROR] stadium={}({}, cause={})",
                                                stadium.getName(), stadium.getId(), e.getMessage()))
                                .onErrorResume(e -> Mono.empty())
                );
    }

    private StadiumWeatherDto parseAll(KmaClient.UltraSrtFcstAllResponse resp, Stadium stadium) {
        // 1) 숫자로 바꿀 때 사용할 헬퍼
        Function<String, Double> parseNum = raw -> {
            if (raw == null || raw.contains("강수없음")) {
                return 0.0;
            }
            // "4.0mm", "1mm 미만" 등 → 숫자와 소수점만 남김
            String cleaned = raw.replaceAll("[^0-9.]", "");
            if (cleaned.isEmpty()) return 0.0;
            return Double.parseDouble(cleaned);
        };

        double temp = 0, hum = 0, rain = 0, wsd = 0, vec = 0;
        String sky = "알수없음";

        for (var it : resp.getResponse().getBody().getItems().getItem()) {
            String cat = it.getCategory();
            String val = it.getFcstValue();

            switch (cat) {
                case "T1H" -> temp = parseNum.apply(val);
                case "REH" -> hum  = parseNum.apply(val);
                case "RN1" -> rain = parseNum.apply(val);
                case "WSD" -> wsd  = parseNum.apply(val);
                case "VEC" -> vec  = parseNum.apply(val);
                case "SKY" -> {
                    sky = switch (val) {
                        case "1" -> "맑음";
                        case "3" -> "구름 많음";
                        case "4" -> "흐림";
                        default  -> "알수없음";
                    };
                }
                // PTY 등 다른 카테고리는 무시
            }
        }

        return StadiumWeatherDto.builder()
                .stadiumId(stadium.getId())
                .stadiumName(stadium.getName())
                .temperature(temp)
                .humidity(hum)
                .precipitation(rain)
                .windSpeed(wsd)
                .windDirection(vec)
                .condition(sky)
                .build();
    }
}
