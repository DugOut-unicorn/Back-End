package dugout.DugOut.repository;

import dugout.DugOut.domain.WinProbability;
import dugout.DugOut.domain.WinProbabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WinProbabilityRepository
        extends JpaRepository<WinProbability, WinProbabilityId> {
    Optional<WinProbability> findByTeam1AndTeam2(
            String team1,
            String team2
    );
}