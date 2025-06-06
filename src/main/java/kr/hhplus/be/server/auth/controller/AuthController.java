package kr.hhplus.be.server.auth.controller;

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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignUpRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
