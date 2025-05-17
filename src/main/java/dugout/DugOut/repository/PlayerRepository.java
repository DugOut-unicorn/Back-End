package dugout.DugOut.repository;

import dugout.DugOut.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // 전체 선수 조회 (등번호 오름차순)
    List<Player> findTop20ByTeamIdxAndBackNumberIsNotNullOrderByBackNumberAsc(Integer teamIdx);
    
    // 투수 조회 (등번호 오름차순)
    List<Player> findTop10PitchersByTeamIdxAndPositionAndBackNumberIsNotNullOrderByBackNumberAsc(Integer teamIdx, String position);
    
    // 내야수 조회 (등번호 오름차순)
    List<Player> findTop10InfieldersByTeamIdxAndPositionAndBackNumberIsNotNullOrderByBackNumberAsc(Integer teamIdx, String position);
    
    // 외야수 조회 (등번호 오름차순)
    List<Player> findTop10OutfieldersByTeamIdxAndPositionAndBackNumberIsNotNullOrderByBackNumberAsc(Integer teamIdx, String position);
    
    // 포수 조회 (등번호 오름차순)
    List<Player> findTop10CatchersByTeamIdxAndPositionAndBackNumberIsNotNullOrderByBackNumberAsc(Integer teamIdx, String position);
} 