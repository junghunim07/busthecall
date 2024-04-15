package capston.busthecall.domain;


import capston.busthecall.domain.Status.DoingStatus;
import javax.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="reservation_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private Long bus_id;

    private Long station_id;

    @CreationTimestamp
    @Column(name = "reserveTime")
    private LocalDateTime reserveTime;

    @Enumerated(EnumType.STRING)
    private DoingStatus status;

    @Builder
    public Reservation(Member member, Long bus_id, LocalDateTime reserveTime, Long station_id, DoingStatus status)
    {
        this.member =member;
        this.bus_id = bus_id;
        this.reserveTime = reserveTime;
        this.station_id = station_id;
        this.status = status;
    }

}
