package dugout.DugOut.service;

import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.repository.MatchingPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingPostService {
    private final MatchingPostRepository repo;

    public MatchingPostService(MatchingPostRepository repo) {
        this.repo = repo;
    }

    /**
     * 최근 5개 매칭글 조회
     */
    public List<MatchingPost> getRecentPosts() {
        return repo.findTop5ByOrderByCreatedAtDesc();
    }
}
