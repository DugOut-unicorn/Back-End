package dugout.DugOut.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class TeamCodeMapping {

    /**
     * key: 2글자 약어 (ex: "SS", "LG", "LT", …)
     * value: 팀 풀네임(한국어) (ex: "삼성", "LG", "롯데", …)
     */
    public static final Map<String, String> ABBR_TO_FULL = new HashMap<>();

    static {
        ABBR_TO_FULL.put("SS", "삼성");
        ABBR_TO_FULL.put("LG", "LG");
        ABBR_TO_FULL.put("LT", "롯데");
        ABBR_TO_FULL.put("HT", "KIA");
        ABBR_TO_FULL.put("HH", "한화");
        ABBR_TO_FULL.put("NC", "NC");
        ABBR_TO_FULL.put("SK", "SSG");
        ABBR_TO_FULL.put("OB", "두산");
        ABBR_TO_FULL.put("KT", "KT");
        ABBR_TO_FULL.put("WO", "키움");
    }

    /**
     * 약어가 맵에 없을 때 반환할 기본값(Optional)
     */
    public static String getFullName(String abbr) {
        return ABBR_TO_FULL.getOrDefault(abbr, "UNKNOWN");
    }
}