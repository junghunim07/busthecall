package capston.busthecall.domain.dto.response;


import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CountReservationInfo {

    private int onboard;
    private int offboard;
}
