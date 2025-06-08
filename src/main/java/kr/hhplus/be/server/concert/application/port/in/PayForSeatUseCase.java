package kr.hhplus.be.server.concert.application.port.in;

public interface PayForSeatUseCase {
    void pay(Long userId, Long reservationId, int amount);
}
