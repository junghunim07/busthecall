package capston.busthecall.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Driver {

    @Id @Column(name = "driverId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String firebase;

    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY)
    private Bus bus;
}
