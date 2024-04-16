package capston.busthecall.security.configuration;

import capston.busthecall.security.jwt.JwtFilter;
import capston.busthecall.security.jwt.JwtUtil;
import capston.busthecall.security.jwt.LoginFilter;
import capston.busthecall.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * 인가 작업
     * csrf 설정
     * 로그인 방식 설정
     * 세션 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());

        //csrf disable
        http.csrf((auth) -> auth.disable());

/*
        //Spring Security HTTP 보안 구성에 CORS 적용
        http.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            // Cors 구성
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();

                // "http://localhost:8080" 요청만 허용하는 출처 설정
                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
                //허용하는 HTTP 메소드 설정
                configuration.setAllowedMethods(Collections.singletonList("*"));
                //자격 증명 허용 설정
                configuration.setAllowCredentials(true);
                //HTTP 헤더 설정
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                //pre-flight 응답을 캐시할 시간 설정 -> 초 단위
                configuration.setMaxAge(3600L);

                //브라우저에 노출할 헤더 설정
                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                return configuration;
            }
        })));
*/
        //From 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());

        /**
         * 인가 작업
         * anyRequest().authenticated() -> 나머지 요청에 대해서는 로그인한 사용자만 접근 가능하도록 설정.
         */
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/v1/drivers/join", "/api/v1/drivers/login", "/reissue").permitAll()
                .anyRequest().authenticated());

        //JWTFilter 등록
        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        /**
         * 필터 등록
         * FilterAt -> 해당 Filter 자리를 지정
         * FilterBefore -> 해당 Filter 전
         * FilterAfter -> 해당 Filter 후
         */
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenService)
                , UsernamePasswordAuthenticationFilter.class);

        //JWT -> 세션을 항상 stateless 로 관리.
        //세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}