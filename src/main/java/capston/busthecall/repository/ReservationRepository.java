package capston.busthecall.repository;


import capston.busthecall.domain.Reservation;
import capston.busthecall.domain.status.DoingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByMemberId(Long memberId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.stationId = :stationId AND r.status = :status")
    Long countByStationIdAndStatus(Long stationId, DoingStatus status);
}
