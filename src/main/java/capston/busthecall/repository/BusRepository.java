package capston.busthecall.repository;

import capston.busthecall.domain.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {
}
