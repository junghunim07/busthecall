package capston.busthecall.security.filter;

import capston.busthecall.security.authentication.authority.Roles;
import capston.busthecall.security.dto.custom.CustomDetails;
import capston.busthecall.security.dto.TokenResponse;
import capston.busthecall.security.support.ResponseMessage;
import capston.busthecall.security.support.TokenResponseSetting;
import capston.busthecall.security.token.AuthToken;
import capston.busthecall.security.token.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

/**
 * 요청을 가로채서 요청 값을 검증
 */
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenGenerator;

    public LoginFilter(AuthenticationManager authenticationManager, TokenProvider tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.tokenGenerator = tokenGenerator;
        this.setFilterProcessesUrl("/api/v1/login");
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 email, password 추출
        Map<String, String> requestBody = obtainBody(request);
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        email = email.trim();

        //스프링 시큐리티에서 email, password 검증하기 위해서는 token 에 담아야 함.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        setDetails(request, authToken);

        // token 에 담은 검증을 위한 AuthenticationManager 전달.
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        String email = getUserEmailIn(authentication);
        String role = getUserRoleIn(authentication);

        log.info("Login Filter : email = {}, role = {}", email, role);

        AuthToken token = tokenGenerator.createJwt(email, Roles.roleOf(role));

        TokenResponse tokenResponse = setResponseToken(HttpStatus.OK, ResponseMessage.LOGIN_SUCCESS, token);

        //응답 설정
        setResponseBody(response, tokenResponse);
    }

    private String getUserEmailIn(Authentication authentication) {
        CustomDetails customDetails = (CustomDetails) authentication.getPrincipal();
        return customDetails.getEmail();
    }

    private String getUserRoleIn(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        return auth.getAuthority();
    }

    //로그인 실패시 실행하는 메소드

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        TokenResponse tokenResponse = setResponseToken(HttpStatus.UNAUTHORIZED, ResponseMessage.LOGIN_FAIL, null);
        setResponseBody(response, tokenResponse);
    }
    protected Map<String, String> obtainBody(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setResponseBody(HttpServletResponse response, TokenResponse token) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(token);

        response.setContentType(TokenResponseSetting.CONTENT_TYPE_JSON.getSetting());
        response.setCharacterEncoding(TokenResponseSetting.CHARACTER_ENCODING_UTF8.getSetting());
        response.getWriter().write(json);
    }

    private TokenResponse setResponseToken(HttpStatus status, ResponseMessage message, AuthToken token) {

        return TokenResponse.builder()
                .status(status)
                .message(message)
                .token(token)
                .build();
    }
}
