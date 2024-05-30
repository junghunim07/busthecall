package capston.busthecall.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ReservationResponse {

    private Long reservationId;
    private String memberName;
    private String routeName;
    private String stationName;
}
