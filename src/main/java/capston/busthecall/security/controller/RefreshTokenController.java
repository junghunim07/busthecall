package capston.busthecall.security.controller;

import capston.busthecall.security.authentication.authority.Roles;
import capston.busthecall.security.dto.TokenResponse;
import capston.busthecall.security.support.ResponseMessage;
import capston.busthecall.security.token.AuthToken;
import capston.busthecall.security.token.TokenName;
import capston.busthecall.security.token.TokenProvider;
import capston.busthecall.security.token.TokenResolver;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final TokenResolver tokenResolver;
    private final TokenProvider tokenProvider;

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request, HttpServletResponse response){

        //get refresh token
        String refresh = getRefreshToken(request);
        HttpHeaders header = responseHeaderSetting();

        String email = tokenResolver.getEmail(refresh);
        String role = tokenResolver.getRole(refresh);

        log.info("RefreshToken Controller : email = {}, role = {}", email, role);

        AuthToken token = tokenProvider.createJwt(email, Roles.roleOf(role));
        TokenResponse tokenResponse = createTokenResponse(token);

        return new ResponseEntity<>(tokenResponse, header, tokenResponse.getStatus());
    }

    private static HttpHeaders responseHeaderSetting() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return header;
    }

    private TokenResponse createTokenResponse(AuthToken token) {

        return TokenResponse.builder()
                .status(HttpStatus.OK)
                .message(ResponseMessage.REISSUE_REFRESH_TOKEN)
                .token(token)
                .build();
    }

    private static String getRefreshToken(HttpServletRequest request) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TokenName.REFRESH.getName())) {
                refresh = cookie.getValue();
            }
        }
        return refresh;
    }
}
