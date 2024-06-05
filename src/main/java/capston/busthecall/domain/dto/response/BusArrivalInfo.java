package capston.busthecall.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class BusArrivalInfo {
    private Long busId;
    private int remainStop;
    private Long nowBusStopId;
    private String busstopName;

    private String shortLineName;

    private Long remainMin;

    private Long lineId;

    private int arriveFlag;
    @Builder
    public BusArrivalInfo(Long busId, int remainStop, Long nowBusStopId, String busstopName, String shortLineName, Long remainMin, Long lineId, int arriveFlag) {
        this.busId = busId;
        this.remainStop = remainStop;
        this.nowBusStopId = nowBusStopId;
        this.busstopName = busstopName;
        this.shortLineName = shortLineName;
        this.remainMin = remainMin;
        this.lineId = lineId;
        this.arriveFlag=arriveFlag;
    }
}
