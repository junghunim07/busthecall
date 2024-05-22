package capston.busthecall.domain;


import capston.busthecall.domain.status.BoardingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="memberId")
    private Long id;

    private String password;
    private String name;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private BoardingStatus status;
}
