package capston.busthecall.domain;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Reservation {


    @Id
    @GeneratedValue
    @Column(name = "reservation_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
