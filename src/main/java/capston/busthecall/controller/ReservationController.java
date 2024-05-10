package capston.busthecall.controller;


import capston.busthecall.domain.dto.request.CreateReservationRequest;
import capston.busthecall.domain.dto.response.CountReservationInfo;
import capston.busthecall.domain.dto.response.DeletedReservationInfo;
import capston.busthecall.domain.dto.response.SavedInfo;
import capston.busthecall.security.token.TokenResolver;
import capston.busthecall.service.ReservationService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
   // private final TokenUserDetailsService tokenUserDetailsService;
    //private final TokenResolver tokenResolver;

    @PostMapping("/onboard")
    public String save(
            @RequestBody @Valid CreateReservationRequest requestData, HttpServletRequest request) {
        Long memberId = 0L; //findMemberByToken(request);
        Long reservationId = reservationService.execute(requestData, memberId);
        log.info("reservationId={}",reservationId);
        return "redirect:/api/v1/reservations";
    }

    @GetMapping("/offboard")
    public ApiResponse<ApiResponse.SuccessBody<DeletedReservationInfo>> delete()
    {
        Long memberId = null; // findMemberByToken(request);
        DeletedReservationInfo res = reservationService.excute2(memberId);
        return ApiResponseGenerator.success(res, HttpStatus.CREATED);
    }

//    @GetMapping("/{stationId}")
//    public ApiResponse<ApiResponse.SuccessBody<CountReservationInfo>> count(@PathVariable Long stationId, HttpServletRequest request)
//    {
//        Long drvierId = null; //findMemberByToken(request);
//
//        CountReservationInfo res = reservationService.excute3(stationId,drvierId);
//
//        return ApiResponseGenerator.success(res, HttpStatus.CREATED);
//    }

//    private Long findMemberByToken(HttpServletRequest request) {
//        String authorization = request.getHeader("Authorization");
//        String substring = authorization.substring(7, authorization.length());
//        UserDetails userDetails = tokenUserDetailsService.loadUserByUsername(substring);
//        Long memberId = Long.parseLong(userDetails.getUsername());
//        return memberId;
//    }
}
