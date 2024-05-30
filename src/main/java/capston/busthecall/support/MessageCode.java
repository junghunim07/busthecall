package capston.busthecall.support;

public enum MessageCode {
    SUCCESS("success", "성공"),
    RESOURCE_DELETED("resource.deleted", "삭제되었습니다."),
    RESOURCE_CREATED("resource.created", "새로 생성되었습니다."),
    RIDE_RESERVATION_CREATED("ride.reservation.created", "승차 예약되었습니다."),
    NOT_FOUND_RESERVATION("reservation.not.existed", "예약된 정보가 없습니다."),
    NOT_FOUND_BUS("bus.not.existed", "운행중인 버스가 아닙니다."),
    FINISH_OPERATE_BUS("bus.operate.finish", "운행이 종료되었습니다."),
    NOT_FOUND_ROUTE_NAME("route.name.not.found", "노선 정보가 없습니다."),
    DROP_RESERVATION_CREATED("drop.reservation.created", "하차 예약되었습니다."),
    ALREADY_EXIST("resource.existed", "이미 존재합니다.");

    private final String code;
    private final String value;

    MessageCode(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }
}