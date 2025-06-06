package kr.hhplus.be.server.auth.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.auth.dto.LoginResponse;
import kr.hhplus.be.server.auth.entity.RefreshToken;
import kr.hhplus.be.server.auth.repository.RefreshTokenRepository;
import kr.hhplus.be.server.common.response.error.CustomException;
import kr.hhplus.be.server.common.security.JwtTokenProvider;
import kr.hhplus.be.server.user.entity.User;
import kr.hhplus.be.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "사용자 없음"));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "비밀번호 불일치");
        }

        String atk = jwtTokenProvider.createAccessToken(username, List.of("ROLE_USER"));
        String rtk = jwtTokenProvider.createRefreshToken(username);

        return new LoginResponse(atk, rtk);
    }

    public String reissueAccessToken(String rtk) {
        if (!jwtTokenProvider.validateToken(rtk)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰");
        }

        String username = jwtTokenProvider.getUsernameFromToken(rtk);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "사용자 없음"));
        RefreshToken refreshToken = refreshTokenRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰"));

        if (!rtk.equals(refreshToken.getToken())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "DB에 저장된 토큰과 불일치");
        }

        return jwtTokenProvider.createAccessToken(username, List.of("ROLE_USER"));
    }
}
