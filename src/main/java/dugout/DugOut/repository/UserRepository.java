package dugout.DugOut.repository;

import dugout.DugOut.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * 주어진 userIdx가 응원하는 팀 ID 반환
     * (DB 컬럼명: cheering_team_id → 엔티티 필드: cheeringTeamId)
     */
    @Query("select u.cheeringTeamId from User u where u.userIdx = :userIdx")
    Integer findCheeringTeamIdByUserIdx(@Param("userIdx") Integer userIdx);

    Optional<User> findByEmail(String email);
}
