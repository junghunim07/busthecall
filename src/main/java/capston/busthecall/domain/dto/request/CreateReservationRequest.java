package capston.busthecall.domain.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequest {

    private Long bus_id;

    private Long station_id;

}
