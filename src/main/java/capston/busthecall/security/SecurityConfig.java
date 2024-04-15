package capston.busthecall.security;

import capston.busthecall.security.authentication.token.TokenAuthProvider;
import capston.busthecall.security.filter.token.TokenAuthenticationFilter;
import capston.busthecall.security.handler.DelegatedAccessDeniedHandler;
import capston.busthecall.security.handler.DelegatedAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final DelegatedAuthenticationEntryPoint authenticationEntryPoint;
    private final DelegatedAccessDeniedHandler accessDeniedHandler;
    private final TokenAuthProvider tokenAuthProvider;

    @Bean
    @Profile("!prod")
    public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();
        http.cors().configurationSource(corsConfigurationSource());
        http.authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/swagger-ui/*",
                        "/api-docs/*",
                        "/openapi3.yaml",
                        "/actuator/health",
                        "/reports/**",
                        "/error",
                        "/beta/**",
                        "/api/v1/list/*"
                )
                .permitAll()
                .antMatchers(
                        HttpMethod.POST,
                        "/api/v1/members/login",
                        "/api/v1/members/register",
                        "/api/v1/members/tokens",
                        "/beta/*")
                .permitAll()
                .antMatchers("/test/**")
                .permitAll()
                .antMatchers("/api/v1/**")
                .authenticated()
                .anyRequest()
                .denyAll();

        http.addFilterAt(
                generateAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @Bean
    @Profile(value = "prod")
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();
        http.cors().configurationSource(corsConfigurationSource());

        http.authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/openapi3.yaml",
                        "/actuator/health",
                        "/reports/**",
                        "/error",
                        "/beta/**")
                .permitAll()
                .antMatchers(
                        HttpMethod.POST,
                        "/api/v1/members/login",
                        "/api/v1/members/register",
                        "/api/v1/members/tokens",
                        "/beta/*")
                .permitAll()
                .antMatchers("/api/v1/**")
                .authenticated()
                .anyRequest()
                .denyAll();

        http.addFilterAt(
                generateAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    public TokenAuthenticationFilter generateAuthenticationFilter() {
        TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter();
        tokenAuthenticationFilter.setAuthenticationManager(new ProviderManager(tokenAuthProvider));
        return tokenAuthenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
