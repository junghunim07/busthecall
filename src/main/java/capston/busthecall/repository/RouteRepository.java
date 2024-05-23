package capston.busthecall.repository;

import capston.busthecall.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findById(Long routeId);

    Optional<Route> findByName(String routeName);
}
