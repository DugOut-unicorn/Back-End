package dugout.DugOut.repository;

import dugout.DugOut.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, Integer> {
} 