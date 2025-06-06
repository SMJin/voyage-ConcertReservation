package kr.hhplus.be.server.concert.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.concert.dto.ConcertSearchCond;
import kr.hhplus.be.server.concert.entity.Concert;
import kr.hhplus.be.server.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public Concert getOrThrow(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Concert> search(ConcertSearchCond cond) {
        List<Concert> concerts = concertRepository.findAll();

        return concerts.stream()
                .filter(c -> cond.getName() == null || c.getName().contains(cond.getName()))
                .filter(c -> cond.getDateFrom() == null || !c.getDate().toLocalDate().isBefore(cond.getDateFrom()))
                .filter(c -> cond.getDateTo() == null || !c.getDate().toLocalDate().isAfter(cond.getDateTo()))
                .filter(c -> cond.getVenue() == null || c.getVenue().contains(cond.getVenue()))
                .filter(c -> cond.getPriceFrom() == -1 || c.getPrice() >= cond.getPriceFrom())
                .filter(c -> cond.getPriceTo() == -1 || c.getPrice() <= cond.getPriceTo())
                .collect(Collectors.toList());
    }
}
