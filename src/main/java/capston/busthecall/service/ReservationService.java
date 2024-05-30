package capston.busthecall.service;


import capston.busthecall.domain.Member;
import capston.busthecall.domain.Reservation;
import capston.busthecall.domain.dto.response.DeletedReservationInfo;
import capston.busthecall.domain.dto.response.ReservationResponse;
import capston.busthecall.domain.status.DoingStatus;
import capston.busthecall.domain.dto.request.CreateReservationRequest;
import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.repository.MemberRepository;
import capston.busthecall.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final BusService busService;

    private final int RESERVATION_CONFIRM = 1;
    private final int RESERVATION_CANCEL = -1;
    private final int ZERO = 0;
    LocalDateTime now = LocalDateTime.now();
    @Transactional
    public ReservationResponse rideReservation(CreateReservationRequest request, Long memberId) {

        Reservation reservation = createReservation(request, memberId, DoingStatus.BOARD);
        reservationRepository.save(reservation);

        return createResponse(reservation);
    }

    @Transactional
    public ReservationResponse dropReservation(CreateReservationRequest request, Long memberId) {

        Reservation reservation = createReservation(request, memberId, DoingStatus.DROP);
        busService.update(reservation.getBusId(), RESERVATION_CONFIRM, ZERO);
        reservationRepository.save(reservation);

        return createResponse(reservation);
    }


    @Transactional
    public DeletedReservationInfo cancel(Long memberId) {
        Reservation reservation = getReservation(memberId);

        updateCancelCount(reservation);
        reservationRepository.delete(reservation);

        return DeletedReservationInfo.builder()
                .isCancel(true)
                .build();
    }

    private ReservationResponse createResponse(Reservation reservation) {

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .memberName(reservation.getMember().getName())
                .stationName(reservation.getStationName())
                .routeName(busService.findOne(reservation.getBusId()).getRoute().getName())
                .build();
    }

    private Reservation createReservation(CreateReservationRequest request, Long memberId, DoingStatus status) {

        Optional<Member> member = memberRepository.findById(memberId);

        if (busService.findOne(request.getBusId()) == null) {
            throw new AppException(ErrorCode.NOT_FOUND_OPERATING_BUS, "not operating Bus");
        }

        return member.map(value -> Reservation.builder()
                .member(value)
                .busId(request.getBusId())
                .stationName(request.getStationName())
                .reserveTime(LocalDateTime.now())
                .status(status)
                .build()).orElse(null);
    }

    private void updateCancelCount(Reservation reservation) {
        if (reservation.getStatus().equals(DoingStatus.BOARD)) {
            busService.update(reservation.getBusId(), RESERVATION_CANCEL, ZERO);
        }
        if (reservation.getStatus().equals(DoingStatus.DROP)) {
            busService.update(reservation.getBusId(), ZERO, RESERVATION_CANCEL);
        }
    }

    private Reservation getReservation(Long memberId) {
        Optional<Reservation> reservationOptional = reservationRepository.findByMemberId(memberId);

        if (reservationOptional.isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND_RESERVATION, "not existed reservation");
        }
        return reservationOptional.get();
    }
}
