package capston.busthecall.security.filter;

import capston.busthecall.domain.Driver;
import capston.busthecall.domain.Member;
import capston.busthecall.security.authentication.authority.Roles;
import capston.busthecall.security.dto.custom.CustomDetails;
import capston.busthecall.security.dto.custom.CustomDriverDetails;
import capston.busthecall.security.dto.custom.CustomMemberDetails;
import capston.busthecall.security.token.TokenName;
import capston.busthecall.security.token.TokenResolver;
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
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenResolver tokenResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request 에서 access 헤더 찾기.
        String accessToken = request.getHeader(TokenName.ACCESS.getName());

        log.info("accessToken = {}", accessToken);

        /**
         * checkAccessHeader -> access 헤더 검증
         * checkTokenExpired -> token 만료 여부
         * checkAccessToken -> token 이 access 인지 확인. 맞으면 -> true 틀리면 -> false
         */
        if (checkAccessHeader(request, response, filterChain, accessToken)
                || checkTokenExpired(response, accessToken)
                || !checkAccessToken(response, accessToken)) {
            log.info("request denied");
            return;
        }

        String role = tokenResolver.getRole(accessToken);
        CustomDetails customDetails = null;

        log.info("role = {}", role);

        if (role.equals(Roles.MEMBER.getRole())) {
            log.info("MEMBER USER");
            Member member = getMemberInToken(accessToken);
            customDetails = new CustomMemberDetails(member);
        }

        if (role.equals(Roles.DRIVER.getRole())) {
            log.info("DRIVER USER");
            Driver driver = getDriverInToken(accessToken);
            customDetails = new CustomDriverDetails(driver);
        }

        /*Driver driver = getDriverInToken(accessToken);

        //DriverDetails 버스 기사 정보 담기
        CustomDetails customDriverDetails = new CustomDriverDetails(driver);*/

        //스프링 시큐리티 인증 토큰 생성
        if (customDetails != null) {
            Authentication authToken = new UsernamePasswordAuthenticationToken(customDetails, null, customDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } else {
            log.info("CustomDetails Error = {}", customDetails);
        }
        /**
         * 세션에 사용자 등록
         * JWT 방식의 일시적인 세션 -> 엄밀하게 정의하면 세션 X
         * SecurityContextHolder 가 관리하는 SecurityContext 의 Authentication 객체를 의미.
         * SecurityContext 생명 주기 -> SecurityContextHolderFilter 통과할 때까지 = 요청 후 서버에 머무르는 일시적인 시간.
         */
        //SecurityContextHolder.getContext().setAuthentication(authToken);

        //filterChain.doFilter(request, response);
    }

    private static boolean checkAccessHeader(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String accessToken) throws IOException, ServletException {
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private Member getMemberInToken(String accessToken) {
        String email = tokenResolver.getEmail(accessToken);

        return Member.builder()
                .email(email)
                .build();
    }

    private Driver getDriverInToken(String accessToken) {
        String email = tokenResolver.getEmail(accessToken);

        return Driver.builder()
                .email(email)
                .build();
    }

    private boolean checkAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        String category = tokenResolver.getCategory(accessToken);

        if (!category.equals(TokenName.ACCESS.getName())) {

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
            tokenResolver.isExpired(accessToken);
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
