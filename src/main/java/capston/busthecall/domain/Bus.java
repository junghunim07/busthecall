package capston.busthecall.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Bus {

    @Id
    @GeneratedValue
    @Column(name = "bus_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "driver")
    private Driver driver;

    @OneToMany(mappedBy = "reservation")
    private List<Reservation> reservations  = new ArrayList<>();


}
