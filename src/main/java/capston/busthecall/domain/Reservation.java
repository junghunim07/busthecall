package capston.busthecall.domain;


import capston.busthecall.domain.status.DoingStatus;
import jakarta.persistence.*;
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
    @Column(name ="reservationId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;
    private Long busId;
    private Long stationId;

    @CreationTimestamp
    @Column(name = "reserveTime")
    private LocalDateTime reserveTime;

    @Enumerated(EnumType.STRING)
    private DoingStatus status;

    @Builder
    public Reservation(Member member, Long busId, LocalDateTime reserveTime, Long stationId, DoingStatus status)
    {
        this.member =member;
        this.busId = busId;
        this.reserveTime = reserveTime;
        this.stationId = stationId;
        this.status = status;
    }

}
