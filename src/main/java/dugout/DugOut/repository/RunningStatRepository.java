package dugout.DugOut.repository;

import dugout.DugOut.domain.RunningStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunningStatRepository extends JpaRepository<RunningStat, Integer> {
} 
 