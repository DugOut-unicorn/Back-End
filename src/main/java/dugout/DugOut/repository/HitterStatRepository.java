package dugout.DugOut.repository;

import dugout.DugOut.domain.HitterStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HitterStatRepository extends JpaRepository<HitterStat, Integer> {
    List<HitterStat> findTop3ByTeamIdxOrderByAvgDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByRbiDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByGDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByPaDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByAbDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByRDesc(Integer teamIdx);
    List<HitterStat> findTop3ByTeamIdxOrderByHDesc(Integer teamIdx);
    HitterStat findByPlayerIdx(Integer playerIdx);

    @Query("SELECT h FROM HitterStat h ORDER BY h.pa DESC LIMIT 3")
    List<HitterStat> findTop3HittersByPa();

    @Query("SELECT h FROM HitterStat h ORDER BY h.rbi DESC LIMIT 3")
    List<HitterStat> findTop3HittersByRbi();

    @Query("SELECT h FROM HitterStat h ORDER BY h.avg DESC LIMIT 3")
    List<HitterStat> findTop3HittersByAvg();

    @Query("SELECT h FROM HitterStat h ORDER BY h.hr DESC LIMIT 3")
    List<HitterStat> findTop3HittersByHr();

    @Query("SELECT h FROM HitterStat h ORDER BY h.h DESC LIMIT 3")
    List<HitterStat> findTop3HittersByH();
} 