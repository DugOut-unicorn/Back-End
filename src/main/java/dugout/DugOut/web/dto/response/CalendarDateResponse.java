package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CalendarDateResponse {
    private final int year;
    private final int month;
    private final List<Integer> days;
}