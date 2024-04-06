package capston.busthecall.domain;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Driver {

    @Id
    @GeneratedValue
    @Column(name = "driver_id")
    private Long id;

    private String name;

    @OneToOne(mappedBy = "driver")
    private Bus bus;

}
