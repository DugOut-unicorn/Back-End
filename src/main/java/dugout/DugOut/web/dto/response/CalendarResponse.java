package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CalendarResponse {
    private final LocalDate date;
}

