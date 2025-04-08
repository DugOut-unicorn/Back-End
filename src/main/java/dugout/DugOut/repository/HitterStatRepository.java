package dugout.DugOut.repository;

import dugout.DugOut.domain.HitterStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HitterStatRepository extends JpaRepository<HitterStat, Integer> {
} 