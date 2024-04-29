package capston.busthecall.service;


import capston.busthecall.domain.Reservation;
import capston.busthecall.domain.status.DoingStatus;
import capston.busthecall.domain.dto.request.CreateReservationRequest;
import capston.busthecall.repository.BusRepository;
import capston.busthecall.repository.MemberRepository;
import capston.busthecall.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final BusRepository busRepository;
    LocalDateTime now = LocalDateTime.now();
    @Transactional
    public Long execute(CreateReservationRequest request, Long originalMemberId) {
        // Member 찾기 시도, 없으면 memberId를 0으로 설정
        Long memberId = memberRepository.findById(originalMemberId)
                .map(member -> member.getId())
                .orElse(0L); // Member가 없으면 0 반환

        Reservation reservation = Reservation.builder()
                .member(memberId == 0 ? null : memberRepository.findById(memberId).get()) // memberId가 0이면 Member를 null로 설정
                .bus_id(request.getBus_id())
                .station_id(request.getStation_id())
                .reserveTime(LocalDateTime.now())
                .status(DoingStatus.valueOf("DROP"))
                .build();

        return reservationRepository.save(reservation).getId();
    }
}
