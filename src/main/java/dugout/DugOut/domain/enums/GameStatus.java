package dugout.DugOut.domain.enums;

public enum GameStatus {
    /** 아직 시작 전인 경기 */
    SCHEDULED("예정"),
    /** 현재 진행 중인 경기 */
    IN_PROGRESS("진행중"),
    /** 이미 종료된 경기 */
    FINISHED("종료");

    private final String displayName;

    GameStatus(String displayName) {
        this.displayName = displayName;
    }

    /** 화면 또는 로그에 표시할 한글 이름을 반환 */
    public String getDisplayName() {
        return displayName;
    }
}
