package dugout.DugOut.repository;

import dugout.DugOut.domain.MatchingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingPostRepository extends JpaRepository<MatchingPost, Integer> {
} 