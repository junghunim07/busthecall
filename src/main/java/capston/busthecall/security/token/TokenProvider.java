package capston.busthecall.security.token;

import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.security.authentication.authority.Roles;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider {

    private SecretKey secretKey;

    @Value("${jwt.access.expiredMs}")
    private Long accessTokenValidTime;

    @Value("${jwt.refresh.expiredMs}")
    private Long refreshTokenValidTime;

    @Autowired
    public TokenProvider(@Value("${jwt.secret}") String key) {
        secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public AuthToken createJwt(Long id, String email, List<Roles> memberRoles) {
        log.info("create JWT Token");
        List<String> roles = convertToStringList(memberRoles);

        return AuthToken.builder()
                .accessToken(generateAccessToken(id, email, roles))
                .refreshToken(generateRefreshToken(id, email, roles))
                .build();
    }

    public String generateAccessToken(Long id, String email, List<String> roles) {
        return Jwts.builder()
                .claim("category", TokenName.ACCESS.getName())
                .claim("id", id)
                .claim("email", email)
                .claim("role", roles.get(0))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long id, String email, List<String> roles) {
        return Jwts.builder()
                .claim("category", TokenName.REFRESH.getName())
                .claim("id", id)
                .claim("email", email)
                .claim("role", roles.get(0))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date((System.currentTimeMillis()) + refreshTokenValidTime))
                .signWith(secretKey)
                .compact();
    }

    private static void checkRoleValidation(List<String> roles) {
        if (roles.isEmpty()) {
            throw new AppException(ErrorCode.NOT_ROLE_IN_EMAIL, "역할이 없습니다.");
        }

        if (roles.size() >= 2) {
            throw new AppException(ErrorCode.MULTI_ROLE_IN_EMAIL, "다중 역할이 존재합니다.");
        }
    }

    private static List<String> convertToStringList(List<Roles> memberRoles) {
        List<String> stringRoles = new ArrayList<>();
        for (Roles role : memberRoles) {
            stringRoles.add(role.getRole());
        }
        checkRoleValidation(stringRoles);

        return stringRoles;
    }
}
