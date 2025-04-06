package dugout.DugOut.repository;

import dugout.DugOut.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Integer> {
} 