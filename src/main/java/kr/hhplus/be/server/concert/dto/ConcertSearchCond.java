package kr.hhplus.be.server.concert.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class ConcertSearchCond {
    private String name;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String venue;
    private int priceFrom = -1;
    private int priceTo = -1;
}

