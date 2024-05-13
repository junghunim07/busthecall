package capston.busthecall.security.configuration;

import capston.busthecall.security.filter.JwtFilter;
import capston.busthecall.security.filter.LoginFilter;
import capston.busthecall.security.service.CustomDetailsService;
import capston.busthecall.security.token.TokenProvider;
import capston.busthecall.security.token.TokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomDetailsService customDetailsService;
    private final TokenResolver tokenResolver;
    private final TokenProvider tokenProvider;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customDetailsService);
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
        http.httpBasic(AbstractHttpConfigurer::disable);

        //csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        /**
         * 인가 작업
         * anyRequest().authenticated() -> 나머지 요청에 대해서는 로그인한 사용자만 접근 가능하도록 설정.
         */
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/v1/drivers/join").permitAll()
                .requestMatchers("/api/v1/login", "/reissue").permitAll()
                .requestMatchers("/api/v1/members/join").permitAll()
                .requestMatchers("/api/v1/list/*").permitAll()
                .requestMatchers("/api/v1/reservations/*").permitAll()
                .anyRequest().authenticated());

        //JWTFilter 등록
        http.addFilterBefore(new JwtFilter(tokenResolver), LoginFilter.class);

        /**
         * 필터 등록
         * FilterAt -> 해당 Filter 자리를 지정
         * FilterBefore -> 해당 Filter 전
         * FilterAfter -> 해당 Filter 후
         */
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), tokenProvider)
                , UsernamePasswordAuthenticationFilter.class);

        //JWT -> 세션을 항상 stateless 로 관리.
        //세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}