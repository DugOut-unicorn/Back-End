package dugout.DugOut.repository;

import dugout.DugOut.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    /**
     * 팀 이름으로 Team 엔티티 조회
     * @param teamName DB의 team_name 컬럼 값
     * @return Optional.empty() 면 매칭되는 팀 없음
     */
    Optional<Team> findByTeamName(String teamName);
} 