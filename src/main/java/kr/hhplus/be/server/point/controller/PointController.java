package kr.hhplus.be.server.point.controller;

import kr.hhplus.be.server.common.response.success.ApiResponse;
import kr.hhplus.be.server.point.dto.PointChargeRequest;
import kr.hhplus.be.server.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<?>> chargePoint(@RequestBody PointChargeRequest request) {
        pointService.chargePoint(request.getUsername(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Long>> getBalance(@RequestParam String username) {
        Long balance = pointService.getBalance(username);
        return ResponseEntity.ok(ApiResponse.success(balance));
    }
}

