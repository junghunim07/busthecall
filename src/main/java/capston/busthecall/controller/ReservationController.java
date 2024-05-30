package capston.busthecall.controller;


import capston.busthecall.domain.dto.request.CreateReservationRequest;
import capston.busthecall.domain.dto.response.DeletedReservationInfo;
import capston.busthecall.domain.dto.response.ReservationResponse;
import capston.busthecall.exception.AppException;
import capston.busthecall.security.token.TokenResolver;
import capston.busthecall.service.ReservationService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import capston.busthecall.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final TokenResolver tokenResolver;

    @PostMapping("/ride")
    public ApiResponse<ApiResponse.SuccessBody<ReservationResponse>> ride(
            @RequestBody @Valid CreateReservationRequest requestData, HttpServletRequest request) {
        Long memberId = findMemberByToken(request);
        try {
            ReservationResponse response = reservationService.rideReservation(requestData, memberId);
            log.info("reservationId={}", response.getReservationId());
            return ApiResponseGenerator.success(response, HttpStatus.CREATED, MessageCode.RIDE_RESERVATION_CREATED);
        } catch (AppException e) {
            log.info("Onboard Reservation Error", e);
            return ApiResponseGenerator.success(null, HttpStatus.UNAUTHORIZED, MessageCode.NOT_FOUND_BUS);
        }
    }

    @GetMapping("/cancel")
    public ApiResponse<ApiResponse.SuccessBody<DeletedReservationInfo>> cancel(HttpServletRequest request) {
        Long memberId = findMemberByToken(request);

        try {
            DeletedReservationInfo res = reservationService.cancel(memberId);
            return ApiResponseGenerator.success(res, HttpStatus.OK);
        } catch (AppException e) {
            log.info("reserve cancel error : not existed reservation In DB", e);
            return ApiResponseGenerator.success(null, HttpStatus.NOT_FOUND, MessageCode.RIDE_RESERVATION_CREATED);
        }
    }

    @PostMapping("/drop")
    public ApiResponse<ApiResponse.SuccessBody<ReservationResponse>> drop(
            @RequestBody @Valid CreateReservationRequest requestData, HttpServletRequest request) {
        Long memberId = findMemberByToken(request);

        try {
            ReservationResponse response = reservationService.dropReservation(requestData, memberId);
            log.info("reservationId={}",response.getReservationId());
            return ApiResponseGenerator.success(response, HttpStatus.CREATED, MessageCode.DROP_RESERVATION_CREATED);
        } catch (AppException e) {
            log.info("Drop Reservation Error", e);
            return ApiResponseGenerator.success(null, HttpStatus.UNAUTHORIZED, MessageCode.NOT_FOUND_BUS);
        }
    }

    private Long findMemberByToken(HttpServletRequest request) {
        return tokenResolver.getId(request.getHeader("access"));
    }
}
