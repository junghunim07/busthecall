package capston.busthecall.service;


import capston.busthecall.domain.Bus;
import capston.busthecall.domain.Driver;
import capston.busthecall.domain.Reservation;
import capston.busthecall.domain.dto.response.CountReservationInfo;
import capston.busthecall.domain.dto.response.DeletedReservationInfo;
import capston.busthecall.domain.status.DoingStatus;
import capston.busthecall.domain.dto.request.CreateReservationRequest;
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
    public Long execute(CreateReservationRequest request, Long memberId) {

        Reservation reservation = Reservation.builder()
                .member(memberId == 0 ? null : memberRepository.findById(memberId).get())
                .busId(request.getBusId())
                .stationId(request.getStationId())
                .reserveTime(LocalDateTime.now())
                .status(DoingStatus.valueOf("BOARD"))
                .build();

        return reservationRepository.save(reservation).getId();
    }

    @Transactional
    public Long execute_2(CreateReservationRequest request, Long memberId) {

        Reservation reservation = Reservation.builder()
                .member(memberId == 0 ? null : memberRepository.findById(memberId).get())
                .busId(request.getBusId())
                .stationId(request.getStationId())
                .reserveTime(LocalDateTime.now())
                .status(DoingStatus.valueOf("DROP"))
                .build();

        return reservationRepository.save(reservation).getId();
    }


    @Transactional
    public DeletedReservationInfo excute2(Long memberId)
    {
        Reservation reservation = reservationRepository.findByMemberId(memberId);
        reservationRepository.delete(reservation);
        return DeletedReservationInfo.builder()
                .isCancel(true)
                .build();

    }


    @Transactional
    public CountReservationInfo excute3(Long stationId, Long driverId)
    {
        Long countBoard = reservationRepository.countByStationIdAndStatus(stationId, DoingStatus.BOARD);
        Long countDrop = reservationRepository.countByStationIdAndStatus(stationId, DoingStatus.DROP);

        return CountReservationInfo.builder()
                .onboard(countBoard)
                .offboard(countDrop)
                .build();

    }
}
