package dugout.DugOut.repository;

import dugout.DugOut.domain.PitcherStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PitcherStatRepository extends JpaRepository<PitcherStat, Integer> {
    // 평균자책점 하위 3명
    List<PitcherStat> findTop3ByTeamIdxOrderByEraAsc(Integer teamIdx);
    
    // 삼진 수 상위 3명
    List<PitcherStat> findTop3ByTeamIdxOrderBySoDesc(Integer teamIdx);
    
    // 평균자책 하위 3명
    List<PitcherStat> findTop3ByTeamIdxOrderByErAsc(Integer teamIdx);
    
    // 세이브 상위 3명
    List<PitcherStat> findTop3ByTeamIdxOrderBySvDesc(Integer teamIdx);
    
    // 승리 상위 3명
    List<PitcherStat> findTop3ByTeamIdxOrderByWDesc(Integer teamIdx);
    
    PitcherStat findByPlayerIdx(Integer playerIdx);

    @Query("SELECT p FROM PitcherStat p ORDER BY p.er DESC LIMIT 3")
    List<PitcherStat> findTop3PitchersByEr();

    @Query("SELECT p FROM PitcherStat p WHERE p.w + p.l > 0 ORDER BY (CAST(p.w AS double) / (p.w + p.l)) DESC LIMIT 3")
    List<PitcherStat> findTop3PitchersByWpct();

    @Query("SELECT p FROM PitcherStat p ORDER BY p.era ASC LIMIT 3")
    List<PitcherStat> findTop3PitchersByEra();

    @Query("SELECT p FROM PitcherStat p ORDER BY p.so DESC LIMIT 3")
    List<PitcherStat> findTop3PitchersBySo();

    @Query("SELECT p FROM PitcherStat p ORDER BY p.sv DESC LIMIT 3")
    List<PitcherStat> findTop3PitchersBySv();
} 