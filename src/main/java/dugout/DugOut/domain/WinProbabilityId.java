package dugout.DugOut.domain;

import jakarta.persistence.IdClass;

import java.io.Serializable;
import java.util.Objects;

public class WinProbabilityId implements Serializable {
    private String team1;
    private String team2;

    public WinProbabilityId() {}

    public WinProbabilityId(String team1, String team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WinProbabilityId that)) return false;
        return Objects.equals(team1, that.team1)
                && Objects.equals(team2, that.team2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team1, team2);
    }
}
