package capston.busthecall.security.jwt;

import capston.busthecall.security.dto.response.TokenResponse;
import capston.busthecall.security.service.RefreshTokenService;
import capston.busthecall.security.dto.CustomDriverDetails;
import capston.busthecall.security.token.TokenName;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

/**
 * 요청을 가로채서 요청 값을 검증
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.setFilterProcessesUrl("/api/v1/drivers/login");
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

        CustomDriverDetails customDriverDetails = (CustomDriverDetails) authentication.getPrincipal();

        String id = String.valueOf(customDriverDetails.getId());
        String name = customDriverDetails.getUsername();
        String email = customDriverDetails.getEmail();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        /*Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();*/

        //refresh 토큰이 repository 저장되어 있으면 삭제
        refreshTokenService.isExistRefreshTokenDelete(email);

        String access = jwtUtil.createJwt(TokenName.ACCESS.getName(), id, name, email, role, jwtUtil.getAccessExpiredMs());
        String refresh = jwtUtil.createJwt(TokenName.REFRESH.getName(), id, name, email, role, jwtUtil.getRefreshExpiredMs());

        TokenResponse token = setResponseToken(access, refresh);

        //Refresh 토큰 저장
        refreshTokenService.save(email, refresh, jwtUtil.getRefreshExpiredMs());

        //응답 설정
        setResponseBody(response, token);
        /*response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());*/
    }

    //로그인 실패시 실행하는 메소드

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }
    protected Map<String, String> obtainBody(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setResponseBody(HttpServletResponse response, TokenResponse token) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    private TokenResponse setResponseToken(String access, String refresh) {
        Map<String, String> jwt = new HashMap<>();
        jwt.put(TokenName.ACCESS.getName(), access);
        jwt.put(TokenName.REFRESH.getName(), refresh);

        TokenResponse token = TokenResponse.builder()
                .status(HttpStatus.OK)
                .message("JWT 토큰 발급")
                .data(jwt)
                .build();

        return token;
    }
}
