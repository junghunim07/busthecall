package capston.busthecall.service;


import capston.busthecall.domain.Bus;
import capston.busthecall.domain.Driver;
import capston.busthecall.domain.Member;
import capston.busthecall.domain.Reservation;
import capston.busthecall.domain.dto.response.CountReservationInfo;
import capston.busthecall.domain.dto.response.DeletedReservationInfo;
import capston.busthecall.domain.dto.response.ReservationResponse;
import capston.busthecall.domain.status.DoingStatus;
import capston.busthecall.domain.dto.request.CreateReservationRequest;
import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.repository.BusRepository;
import capston.busthecall.repository.DriverRepository;
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
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
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
        reservationRepository.save(reservation);

        return createResponse(reservation);
    }


    @Transactional
    public DeletedReservationInfo cancel(Long memberId) {
        Optional<Reservation> reservation = reservationRepository.findByMemberId(memberId);

        if (reservation.isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND_RESERVATION, "not existed reservation");
        }

        reservationRepository.delete(reservation.get());
        return DeletedReservationInfo.builder()
                .isCancel(true)
                .build();
    }

    @Transactional
    public CountReservationInfo count(Long stationId, Long driverId) {
        Long countBoard = reservationRepository.countByStationIdAndStatus(stationId, DoingStatus.BOARD);
        Long countDrop = reservationRepository.countByStationIdAndStatus(stationId, DoingStatus.DROP);

        return CountReservationInfo.builder()
                .onboard(countBoard)
                .offboard(countDrop)
                .build();

    }

    private ReservationResponse createResponse(Reservation reservation) {

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .memberName(reservation.getMember().getName())
                .build();
    }

    private Reservation createReservation(CreateReservationRequest request, Long memberId, DoingStatus status) {

        Optional<Member> member = memberRepository.findById(memberId);

        return member.map(value -> Reservation.builder()
                .member(value)
                .busId(request.getBusId())
                .stationId(request.getStationId())
                .reserveTime(LocalDateTime.now())
                .status(status)
                .build()).orElse(null);

    }
}
