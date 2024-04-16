package capston.busthecall.Controller;


import capston.busthecall.domain.dto.request.CreateReservationRequest;
import capston.busthecall.domain.dto.response.SavedReservationInfo;
import capston.busthecall.service.ReservationService;
import capston.busthecall.support.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
   // private final TokenUserDetailsService tokenUserDetailsService;


    @PostMapping("/onboard")
    public String save(
            @RequestBody @Valid CreateReservationRequest requestData, HttpServletRequest request) {
        Long memberId = 0L; //findMemberByToken(request);
        Long reservationId = reservationService.execute(requestData, memberId);
        log.info("reservationId={}",reservationId);
        return "redirect:/api/v1/reservations";
    }

//    private Long findMemberByToken(HttpServletRequest request) {
//        String authorization = request.getHeader("Authorization");
//        String substring = authorization.substring(7, authorization.length());
//        UserDetails userDetails = tokenUserDetailsService.loadUserByUsername(substring);
//        Long memberId = Long.parseLong(userDetails.getUsername());
//        return memberId;
//    }
}
