package capston.busthecall.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class BusArrivalInfo {
    private Long busId;
    private int remainStop;
    private String busstopName;

    private String shortLineName;

    private Long remainMin;

    private Long lineId;

    @Builder
    public BusArrivalInfo(Long busId, int remainStop, String busstopName, String shortLineName, Long remainMin, Long lineId) {
        this.busId = busId;
        this.remainStop = remainStop;
        this.busstopName = busstopName;
        this.shortLineName = shortLineName;
        this.remainMin = remainMin;
        this.lineId = lineId;
    }
}
