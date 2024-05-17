package capston.busthecall.controller;

import capston.busthecall.domain.dto.request.JoinRequest;
import capston.busthecall.domain.dto.response.SavedInfo;
import capston.busthecall.service.SavedUserService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import capston.busthecall.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members")
public class MemberController {

    private final SavedUserService savedUserService;

    @PostMapping(value = "/join")
    public ApiResponse<ApiResponse.SuccessBody<SavedInfo>> register(
            @Valid @RequestBody JoinRequest request) {
        SavedInfo member = savedUserService.join(request);

        if (!member.getIsRegistered()) {
            return ApiResponseGenerator.success(member, HttpStatus.UNAUTHORIZED, MessageCode.ALREADY_EXIST);
        }

        return ApiResponseGenerator.success(member, HttpStatus.CREATED);
    }
}
