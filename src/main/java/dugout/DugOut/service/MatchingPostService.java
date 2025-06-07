package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.domain.User;
import dugout.DugOut.dto.MatchingPostResponseDto;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.repository.MatchingPostRepository;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.web.dto.request.CreateMatchingPostRequest;
import dugout.DugOut.web.dto.response.MatchingPostDetailResponse;
import dugout.DugOut.web.dto.response.MatchingPostListByGameResponse;
import dugout.DugOut.web.dto.response.MatchingPostResponse;
import dugout.DugOut.web.dto.response.ToggleMatchResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchingPostService {
    private final MatchingPostRepository matchingPostRepository;
    private final UserRepository userRepository;
    private final GameService gameService;

    public MatchingPostService(MatchingPostRepository matchingPostRepository, UserRepository userRepository, GameService gameService) {
        this.matchingPostRepository = matchingPostRepository;
        this.userRepository = userRepository;
        this.gameService = gameService;
    }

    public List<MatchingPostResponse> getRecentPosts() {
        // PageRequest.of(0, 5) 로 상위 5개만 가져옵니다.
        return matchingPostRepository.findTop5WithValidUser(PageRequest.of(0, 10));
    }

    public Long create(CreateMatchingPostRequest req, User user) {
        // 1) gameIdx로 Game 엔티티 조회
        Game game = gameService.getGameById(req.getGameIdx());
        if (game == null) {
            throw new IllegalArgumentException("해당 gameIdx의 경기를 찾을 수 없습니다: " + req.getGameIdx());
        }

        // 2) 경기로부터 날짜, 경기장 ID 꺼내기
        LocalDate gameDate = LocalDate.from(game.getDate());       // 엔티티의 날짜 필드
        Integer stadiumIdx = game.getStadiumIdx();     // 엔티티의 경기장 ID 필드

        // 3) MatchingPost 엔티티 생성 및 값 세팅
        MatchingPost post = new MatchingPost();
        post.setUser(user);
        post.setUserIdx(user.getUserIdx());// 작성자
        post.setCheeringTeamIdx(user.getCheeringTeamId());    // 작성자의 응원 팀
        post.setTitle(req.getTitle());
        post.setContext(req.getContext());
        post.setCreatedAt(LocalDateTime.now());
        post.setGameIdx(req.getGameIdx());
        post.setHaveTicket(req.getHaveTicket());
        post.setIsMatched(false);

        // 4) game에서 가져온 날짜와 경기장 ID를 preferredMatchDate, stadiumIdx에 넣기
        post.setPreferredMatchDate(gameDate);
        post.setStadiumIdx(stadiumIdx);

        // (만약 status 컬럼을 제거했다면 여기서는 건드릴 필요 없음)
        // post.setStatus("OPEN");  // ← status 컬럼을 썼었다면, DTO+엔티티에서 삭제했으므로 이 줄도 없어짐

        MatchingPost saved = matchingPostRepository.save(post);
        return saved.getMatchingPostIdx();
    }

    public List<MatchingPostListByGameResponse> getPostsByGame(int gameIdx, Pageable pageable) {
        return matchingPostRepository.findByGameIdx(gameIdx, pageable)
                .stream()
                .map(p -> new MatchingPostListByGameResponse(
                        p.getMatchingPostIdx(),
                        p.getTitle(),
                        p.getContext(),
                        p.getHaveTicket(),
                        p.getIsMatched(),
                        p.getCreatedAt()
                ))
                .toList();
    }

    /**
     * 매칭글 상세 조회
     */
    public MatchingPostDetailResponse getPostDetail(Long postIdx) {
        MatchingPost post = matchingPostRepository.findById(postIdx.intValue())
                .orElseThrow(() -> new EntityNotFoundException("MatchingPost not found: " + postIdx));

        User author = userRepository.findById(post.getUserIdx())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + post.getUserIdx()));

        return new MatchingPostDetailResponse(post, author.getNickname());
    }

    @Transactional
    public ToggleMatchResponse toggleMatched(Long matchingPostIdx) {
        MatchingPost post = matchingPostRepository.findById(Math.toIntExact(matchingPostIdx))
                .orElseThrow(() -> new IllegalArgumentException(
                        "MatchingPost with idx=" + matchingPostIdx + " not found."
                ));

        // isMatched를 null-safe하게 토글
        Boolean current = post.getIsMatched();
        Boolean newStatus = (current == null) ? Boolean.TRUE : !current;
        post.setIsMatched(newStatus);

        // 변경된 엔티티를 저장 (@Transactional이므로 flush 시점에 반영됨)
        matchingPostRepository.save(post);

        return new ToggleMatchResponse(matchingPostIdx, newStatus);
    }
}
