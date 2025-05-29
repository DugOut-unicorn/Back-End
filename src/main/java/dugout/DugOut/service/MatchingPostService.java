package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.domain.User;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.repository.MatchingPostRepository;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.web.dto.request.CreateMatchingPostRequest;
import dugout.DugOut.web.dto.response.MatchingPostResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MatchingPostService {
    private final MatchingPostRepository matchingPostRepository;
    private final GameRepository gameRepository;

    public MatchingPostService(MatchingPostRepository matchingPostRepository, GameRepository gameRepository) {
        this.matchingPostRepository = matchingPostRepository;
        this.gameRepository = gameRepository;
    }

    public List<MatchingPostResponse> getRecentPosts() {
        // PageRequest.of(0, 5) 로 상위 5개만 가져옵니다.
        return matchingPostRepository.findTop5WithValidUser(PageRequest.of(0, 10));
    }

    public Long create(CreateMatchingPostRequest req, User author) {
        Game game = gameRepository.findById(req.getGameIdx())
                .orElseThrow(() -> new EntityNotFoundException("Game not found: " + req.getGameIdx()));

        MatchingPost post = MatchingPost.builder()
                .userIdx(author.getUserIdx())
                .gameIdx(game.getGameIdx())
                .stadiumIdx(game.getStadiumIdx())
                .cheeringTeamIdx(author.getCheeringTeamId())
                .title(req.getTitle())                  // title
                .context(req.getContext())              // context
                .haveTicket(req.getHaveTicket())        // have_ticket
                .preferredMatchDate(LocalDate.from(game.getDate()))
                .build();

        // ③ 저장 후 ID 리턴
        matchingPostRepository.save(post);
        return post.getMatchingPostIdx();
    }
}
