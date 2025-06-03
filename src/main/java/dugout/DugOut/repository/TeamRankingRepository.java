package dugout.DugOut.repository;


import dugout.DugOut.domain.TeamRankings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRankingRepository extends JpaRepository<TeamRankings, Integer> {

    List<TeamRankings> findByIdBetweenOrderByIdAsc(Integer startId, Integer endId);

}