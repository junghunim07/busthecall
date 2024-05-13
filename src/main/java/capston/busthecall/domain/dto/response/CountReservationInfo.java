package capston.busthecall.domain.dto.response;


import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CountReservationInfo {

    private Long onboard;
    private Long offboard;
}
