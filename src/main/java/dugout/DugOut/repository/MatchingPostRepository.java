package dugout.DugOut.repository;

import dugout.DugOut.web.dto.response.MatchingPostResponse;
import dugout.DugOut.domain.MatchingPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingPostRepository extends JpaRepository<MatchingPost, Integer> {

    /**
     * User가 실제로 매핑된 매칭글만 가져와서 DTO로 바로 변환합니다.
     */
    @Query("""
      select new dugout.DugOut.web.dto.response.MatchingPostResponse(
        p.matchingPostIdx,
        p.title,
        p.stadiumIdx,
        p.gameIdx,
        p.context,
        u.nickname,
        p.status,
        p.createdAt
      )
      from MatchingPost p
      join p.user u
      order by p.createdAt desc
    """)
    List<MatchingPostResponse> findTop5WithValidUser(Pageable pageable);
}
