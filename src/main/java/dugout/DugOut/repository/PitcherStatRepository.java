package dugout.DugOut.repository;

import dugout.DugOut.domain.PitcherStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PitcherStatRepository extends JpaRepository<PitcherStat, Integer> {
} 
 