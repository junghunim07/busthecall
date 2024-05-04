package capston.busthecall.repository;


import capston.busthecall.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Reservation findByMemberId(Long memberId);
}
