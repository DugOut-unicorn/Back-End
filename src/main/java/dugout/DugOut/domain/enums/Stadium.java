package dugout.DugOut.domain.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Stadium {
    SEOUL_JONGHAP   (1, "서울종합운동장 야구장",        37.5091, 127.0718, 60, 127),
    DOOSAN_JONGHAP  (7, "서울종합운동장 야구장 (두산)", 37.5091, 127.0718, 60, 127),
    INCHEON_SSG     (2, "인천SSG랜더스필드",          37.3943, 126.6348, 53, 120),
    DAEGU_SAMSUNG   (3, "대구삼성라이온즈파크",        35.8726, 128.6025, 89,  91),
    SUWON_KT        (4, "수원KT위즈파크",            37.2673, 127.0098, 60, 109),
    SAJIK           (5, "사직야구장",                35.1635, 129.0551, 98,  76),
    CHANGWON_NC     (6, "창원NC파크",                35.2043, 128.5744, 98,  74),
    GOCHUK_SKY      (8, "고척스카이돔",              37.5081, 126.8640, 60, 132),
    GWANGJU_KIA     (9, "광주기아챔피언스필드",      35.1390, 126.8526, 58,  74),
    DAEJEON_HANHWA (10,"대전한화생명볼파크",        36.3247, 127.4260, 67, 100);

    private final int id;
    private final String name;
    private final double lat;    // for OpenWeatherMap
    private final double lon;    // for OpenWeatherMap
    private final int nx;        // for KMA(grid-X)
    private final int ny;        // for KMA(grid-Y)
}


