package dugout.DugOut.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;



@Getter
public enum Team {
    LG(1, "LG"),
    SSG(2, "SSG"),
    삼성(3, "삼성"),
    KT(4, "KT"),
    롯데(5, "롯데"),
    NC(6, "NC"),
    두산(7, "두산"),
    키움(8, "키움"),
    KIA(9, "KIA"),
    한화(10, "한화");

    private final int idx;
    private final String name;

    Team(int idx, String name) {
        this.idx = idx;
        this.name = name;
    }

    public static String getNameByIdx(int idx) {
        return Arrays.stream(values())
                .filter(t -> t.idx == idx)
                .findFirst()
                .map(t -> t.name)
                .orElse("Unknown");
    }
}
