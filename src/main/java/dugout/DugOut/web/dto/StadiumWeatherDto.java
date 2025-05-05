package dugout.DugOut.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class StadiumWeatherDto {
    private int stadiumId;
    private String stadiumName;
    private double temperature;    // T1H
    private double humidity;       // REH
    private double precipitation;  // RN1
    private double windSpeed;      // WSD
    private double windDirection;  // VEC
    private String condition;   // 맑음/구름많음/흐림 등
}