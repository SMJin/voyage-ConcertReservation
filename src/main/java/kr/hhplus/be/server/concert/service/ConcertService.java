package kr.hhplus.be.server.concert.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.concert.entity.Concert;
import kr.hhplus.be.server.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public Concert getOrThrow(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}
