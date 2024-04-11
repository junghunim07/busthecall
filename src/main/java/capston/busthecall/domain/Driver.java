package capston.busthecall.domain;


import javax.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Driver {

    @Id
    @GeneratedValue
    @Column(name = "driver_id")
    private Long id;

    private String name;

    @OneToOne(mappedBy = "driver")
    private Bus bus;

    @Builder
    public Driver(String name, Bus bus) {
        this.name = name;
        this.bus = bus;
    }
}
