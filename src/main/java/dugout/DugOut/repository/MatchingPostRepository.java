package dugout.DugOut.repository;

import dugout.DugOut.domain.MatchingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingPostRepository extends JpaRepository<MatchingPost, Integer> {
    List<MatchingPost> findTop5ByOrderByCreatedAtDesc();
    List<MatchingPost> findByUserIdx(Integer userIdx);
} 