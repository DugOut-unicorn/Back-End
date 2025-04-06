package dugout.DugOut.repository;

import dugout.DugOut.domain.LineUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineUpRepository extends JpaRepository<LineUp, Integer> {
} 