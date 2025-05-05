package dugout.DugOut.repository;

import dugout.DugOut.domain.DefenseStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefenseStatRepository extends JpaRepository<DefenseStat, Integer> {
} 
 