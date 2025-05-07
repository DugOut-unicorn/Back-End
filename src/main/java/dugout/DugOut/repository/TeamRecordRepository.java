package dugout.DugOut.repository;

import dugout.DugOut.domain.TeamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TeamRecordRepository extends JpaRepository<TeamRecord, Integer> {

    /**
     * 주어진 날짜(date)의 team_record를
     * win_rate 내림차순 정렬하여 모두 가져온다.
     */
    List<TeamRecord> findAllByDateOrderByWinRateDesc(LocalDate date);

    @Query("select max(tr.date) from TeamRecord tr")
    LocalDate findLatestDate();
}
