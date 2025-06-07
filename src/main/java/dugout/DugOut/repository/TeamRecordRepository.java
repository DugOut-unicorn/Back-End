package dugout.DugOut.repository;

import dugout.DugOut.domain.TeamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TeamRecordRepository extends JpaRepository<TeamRecord, Long> {

    /**
     * 주어진 날짜(date)의 team_record를
     * win_rate 내림차순 정렬하여 모두 가져온다.
     */
    @Query("SELECT t FROM TeamRecord t WHERE t.date = :date ORDER BY t.winRate DESC")
    List<TeamRecord> findAllByDateOrderByWinRateDesc(@Param("date") LocalDate date);

    @Query("SELECT MAX(t.date) FROM TeamRecord t")
    LocalDate findLatestDate();

    @Query("SELECT t FROM TeamRecord t WHERE t.date = :date AND t.teamIdx = :teamIdx")
    TeamRecord findByDateAndTeamIdx(@Param("date") LocalDate date, @Param("teamIdx") Integer teamIdx);

    @Query("SELECT tr FROM TeamRecord tr WHERE tr.date = (SELECT MAX(tr2.date) FROM TeamRecord tr2) ORDER BY tr.winRate DESC")
    List<TeamRecord> findTop10ByLatestDateOrderByWinRateDesc();
}
