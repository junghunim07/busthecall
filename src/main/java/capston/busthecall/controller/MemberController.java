package capston.busthecall.controller;

import capston.busthecall.domain.dto.request.JoinRequest;
import capston.busthecall.domain.dto.response.SavedInfo;
import capston.busthecall.service.SavedUserService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
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
            @Valid @RequestBody JoinRequest request)
    {
        SavedInfo res = savedUserService.excute(request);
        return ApiResponseGenerator.success(res, HttpStatus.CREATED);
    }
}
