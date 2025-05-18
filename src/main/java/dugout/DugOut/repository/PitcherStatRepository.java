package dugout.DugOut.repository;

import dugout.DugOut.domain.PitcherStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PitcherStatRepository extends JpaRepository<PitcherStat, Long> {
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
} 