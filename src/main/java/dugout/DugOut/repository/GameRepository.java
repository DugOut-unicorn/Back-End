package dugout.DugOut.repository;

import dugout.DugOut.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    // --- 기존 진행 중인 경기 조회
    @Query("""
      select g
        from Game g
       where g.date >= :startOfDay
         and g.date <= :nowDateTime
    """)
    List<Game> findOngoingByPeriod(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("nowDateTime") LocalDateTime nowDateTime
    );

    // --- 임의 기간의 모든 경기 조회
    @Query("""
      select g
        from Game g
       where g.date >= :start
         and g.date <  :end
    """)
    List<Game> findGamesByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );
}


 