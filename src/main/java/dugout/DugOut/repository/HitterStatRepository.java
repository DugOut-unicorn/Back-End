package dugout.DugOut.repository;

import dugout.DugOut.domain.HitterStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HitterStatRepository extends JpaRepository<HitterStat, Integer> {
    List<HitterStat> findTop3ByTeamIdxOrderByAvgDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByRbiDesc(Integer teamIdx);
} 