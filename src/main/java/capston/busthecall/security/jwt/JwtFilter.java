package capston.busthecall.security.jwt;

import capston.busthecall.domain.Driver;
import capston.busthecall.security.dto.CustomDriverDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request 에서 access 헤더 찾기.
        String accessToken = request.getHeader("access");

        /**
         * checkAccessHeader -> access 헤더 검증
         * checkTokenExpired -> token 만료 여부
         * checkAccessToken -> token 이 access 인지 확인. 맞으면 -> true 틀리면 -> false
         */
        if (checkAccessHeader(request, response, filterChain, accessToken) || checkTokenExpired(response, accessToken)
            || !checkAccessToken(response, accessToken)) {
            return;
        }

        Driver driver = getDriverInToken(accessToken);

        //DriverDetails 버스 기사 정보 담기
        CustomDriverDetails customDriverDetails = new CustomDriverDetails(driver);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customDriverDetails, null, customDriverDetails.getAuthorities());
        /**
         * 세션에 사용자 등록
         * JWT 방식의 일시적인 세션 -> 엄밀하게 정의하면 세션 X
         * SecurityContextHolder 가 관리하는 SecurityContext 의 Authentication 객체를 의미.
         * SecurityContext 생명 주기 -> SecurityContextHolderFilter 통과할 때까지 = 요청 후 서버에 머무르는 일시적인 시간.
         */
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private static boolean checkAccessHeader(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String accessToken) throws IOException, ServletException {
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private Driver getDriverInToken(String accessToken) {
        //토큰에서 email, role 획득
        String email = jwtUtil.getEmail(accessToken);
        String role = jwtUtil.getRole(accessToken);

        Driver driver = Driver.builder()
                .email(email)
                .role(role)
                .build();
        return driver;
    }

    private boolean checkAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    private boolean checkTokenExpired(HttpServletResponse response, String accessToken) throws IOException {
        //토큰 만료 여부 확인, 만료 시 다음 필터로 넘기지 않음.
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return true;
        }
        return false;
    }
}
