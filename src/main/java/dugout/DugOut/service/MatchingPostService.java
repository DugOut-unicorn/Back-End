package dugout.DugOut.service;

import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.repository.MatchingPostRepository;
import dugout.DugOut.web.dto.response.MatchingPostResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingPostService {
    private final MatchingPostRepository repo;

    public MatchingPostService(MatchingPostRepository repo) {
        this.repo = repo;
    }

    public List<MatchingPostResponse> getRecentPosts() {
        // PageRequest.of(0, 5) 로 상위 5개만 가져옵니다.
        return repo.findTop5WithValidUser(PageRequest.of(0, 10));
    }
}
