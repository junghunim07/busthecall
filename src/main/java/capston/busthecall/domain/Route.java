package capston.busthecall.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    @Id @Column(name = "routeId")
    private Long id;

    private String name;
    private int kind;
}
