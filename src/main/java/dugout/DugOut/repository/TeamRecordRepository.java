package dugout.DugOut.repository;

import dugout.DugOut.domain.TeamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRecordRepository extends JpaRepository<TeamRecord, Integer> {
} 