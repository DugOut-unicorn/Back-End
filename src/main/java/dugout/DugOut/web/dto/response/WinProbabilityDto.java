package dugout.DugOut.web.dto.response;

public record WinProbabilityDto(
        String homeTeamIdx,
        String awayTeamIdx,
        Double winProbability
) {}
