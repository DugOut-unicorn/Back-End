package dugout.DugOut.web.dto.response;

public class CreateMatchingPostResponse {
    private final Long postIdx;
    private final String message;

    public CreateMatchingPostResponse(Long postIdx) {
        this.postIdx = postIdx;
        this.message = "매칭 글이 성공적으로 등록되었습니다.";
    }
}
