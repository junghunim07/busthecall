package capston.busthecall.domain.dto.response;


import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DeletedReservationInfo {

    private Boolean isCancel;
}
