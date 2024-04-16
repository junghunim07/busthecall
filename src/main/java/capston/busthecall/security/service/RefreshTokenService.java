package capston.busthecall.security.service;

import capston.busthecall.security.dto.response.TokenResponse;
import capston.busthecall.security.jwt.JwtUtil;
import capston.busthecall.security.repository.RefreshRepository;
import capston.busthecall.security.token.RefreshToken;
import capston.busthecall.security.token.TokenName;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;

    public TokenResponse update(String refreshToken) {

        System.out.println(jwtUtil.getEmail(refreshToken));

        if (!refreshTokenValidation(refreshToken) || !isEqualRefreshCategory(refreshToken)) {
            return createResponse(HttpStatus.BAD_REQUEST, "Invalid Refresh Token", null);
        }

        if (!isExistInDB(refreshToken)) {
            return createResponse(HttpStatus.NOT_FOUND, "Not Exist Refresh Token In DB", null);
        }

        Map<String, String> data = createToken(refreshToken);

        return createResponse(HttpStatus.OK, "JWT 토큰 재발급", data);
    }

    public void isExistRefreshTokenDelete(String email) {
        refreshRepository.findByEmail(email)
                .ifPresent((refreshToken) -> {
                    refreshRepository.deleteByRefresh(refreshToken.getRefresh());
                });
    }

    public void save(String email, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshToken);
    }

    private Map<String, String> createToken(String refreshToken) {
        String id = jwtUtil.getId(refreshToken);
        String name = jwtUtil.getName(refreshToken);
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccess = jwtUtil.createJwt(TokenName.ACCESS.getName(), id, name, email, role, jwtUtil.getAccessExpiredMs());
        String newRefresh = jwtUtil.createJwt(TokenName.REFRESH.getName(), id, name, email, role, jwtUtil.getRefreshExpiredMs());

        Map<String, String> data = new HashMap<>();
        data.put(TokenName.ACCESS.getName(), newAccess);
        data.put(TokenName.REFRESH.getName(), newRefresh);

        refreshRepository.deleteByRefresh(refreshToken);
        save(email, newRefresh, jwtUtil.getRefreshExpiredMs());

        return data;
    }

    private TokenResponse createResponse(HttpStatus status, String message, Map<String, String> data) {
        TokenResponse response = TokenResponse.builder()
                .status(status)
                .message(message)
                .data(data)
                .build();

        return response;
    }

    private boolean isExistInDB(String refreshToken) {
        if (!refreshRepository.existsByRefresh(refreshToken)) {
            return false;
        }
        return true;
    }

    private boolean isEqualRefreshCategory(String refreshToken) {
        if (!jwtUtil.getCategory(refreshToken).equals(TokenName.REFRESH.getName())) {
            return false;
        }

        return true;
    }

    private boolean refreshTokenValidation(String refreshToken) {
        if (refreshToken == null) {
            return false;
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {

            return false;
        }
        
        return true;
    }
}
