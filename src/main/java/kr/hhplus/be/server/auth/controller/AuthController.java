package kr.hhplus.be.server.auth.controller;

import kr.hhplus.be.server.auth.dto.LoginRequest;
import kr.hhplus.be.server.auth.dto.LoginResponse;
import kr.hhplus.be.server.auth.service.AuthService;
import kr.hhplus.be.server.common.response.success.ApiResponse;
import kr.hhplus.be.server.user.dto.SignUpRequest;
import kr.hhplus.be.server.user.entity.CustomUserDetails;
import kr.hhplus.be.server.user.entity.User;
import kr.hhplus.be.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/current-user")
    @Cacheable(value = "currentUser", key = "#userDetails.userId") // ex. currentUser::123 ← userId 123인 경우
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        User currentUser = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(currentUser));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(@RequestBody String refreshToken) {
        String newAccessToken = authService.reissueAccessToken(refreshToken);
        Map<String, String> reissuedToken = Map.of("accessToken", newAccessToken);
        return ResponseEntity.ok(ApiResponse.success(reissuedToken));
    }
}
