package capston.busthecall.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Beacon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="beaconId")
    private Long id;
    private String uuId;
    private Long stationId;
    private String stationName;
}
