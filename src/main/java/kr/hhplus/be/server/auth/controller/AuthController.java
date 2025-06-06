package kr.hhplus.be.server.auth.controller;

import kr.hhplus.be.server.auth.dto.LoginRequest;
import kr.hhplus.be.server.auth.dto.LoginResponse;
import kr.hhplus.be.server.auth.service.AuthService;
import kr.hhplus.be.server.common.response.success.ApiResponse;
import kr.hhplus.be.server.user.dto.SignUpRequest;
import kr.hhplus.be.server.user.entity.User;
import kr.hhplus.be.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignUpRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(@RequestBody String refreshToken) {
        String newAccessToken = authService.reissueAccessToken(refreshToken);
        Map<String, String> reissuedToken = Map.of("accessToken", newAccessToken);
        return ResponseEntity.ok(ApiResponse.success(reissuedToken));
    }
}
