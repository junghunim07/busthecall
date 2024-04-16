package capston.busthecall.security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 흠 이건 따로 설정을 해야할지 의문..
 * SecurityConfig 에 있는 CORS 설정도 동일.
 *
 * CORS -> 다른 출처의 자원을 공유할 수 있도록 설정하는 것.
 */
@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000");
    }
}
