package capston.busthecall.domain;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    @Column(name ="member_id")
    private Long id;

    private String nickname;
    private String phone_number;
    private String email;


    @OneToOne(mappedBy = "member")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    private BoardingStatus status;

}
