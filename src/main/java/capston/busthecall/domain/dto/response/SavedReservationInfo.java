package capston.busthecall.domain.dto.response;


import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SavedReservationInfo {

    private Long id;
    private boolean isReserve;
}
