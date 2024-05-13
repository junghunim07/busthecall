package capston.busthecall.repository;

import capston.busthecall.domain.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeaconRepository extends JpaRepository<Beacon, Long> {


    Beacon findBeaconByUuId(String uuId);
}
