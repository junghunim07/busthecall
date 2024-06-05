package capston.busthecall.repository;

import capston.busthecall.domain.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeaconRepository extends JpaRepository<Beacon, Long> {

    Optional<Beacon> findBeaconByStationId(Long stationId);

    Beacon findBeaconByUuId(String uuId);
}
