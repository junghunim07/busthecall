package capston.busthecall.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReservationCountRequestDto {

    private String targetToken;
    private String title;
    private String body;
}
