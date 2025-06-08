package kr.hhplus.be.server.concert.adapter.in.web;

import kr.hhplus.be.server.concert.application.dto.PayForSeatRequest;
import kr.hhplus.be.server.concert.application.dto.ReserveSeatRequest;
import kr.hhplus.be.server.concert.application.port.in.PayForSeatUseCase;
import kr.hhplus.be.server.concert.application.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReserveSeatUseCase reserveSeatUseCase;
    private final PayForSeatUseCase payForSeatUseCase;

    @PostMapping
    public ResponseEntity<?> reserveSeat(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody ReserveSeatRequest request) {
        reserveSeatUseCase.reserve(userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reservationId}/pay")
    public ResponseEntity<?> payForSeat(
            @PathVariable Long reservationId,
            @RequestBody PayForSeatRequest request
    ) {
        payForSeatUseCase.pay(reservationId, request);
        return ResponseEntity.ok().build();
    }
}
