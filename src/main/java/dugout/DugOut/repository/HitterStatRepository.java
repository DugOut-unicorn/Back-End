package dugout.DugOut.repository;

import dugout.DugOut.domain.HitterStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HitterStatRepository extends JpaRepository<HitterStat, Long> {
    List<HitterStat> findTop3ByTeamIdxOrderByAvgDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByRbiDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByGDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByPaDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByAbDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByRDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByHDesc(Integer teamIdx);
    HitterStat findByPlayerIdx(Integer playerIdx);

    @Query("SELECT hs FROM HitterStat hs JOIN Player p ON hs.playerIdx = p.playerIdx ORDER BY hs.avg DESC")
    List<HitterStat> findTop3HittersByAvg();

    @Query("SELECT hs FROM HitterStat hs JOIN Player p ON hs.playerIdx = p.playerIdx ORDER BY hs.rbi DESC")
    List<HitterStat> findTop3HittersByRbi();
} 