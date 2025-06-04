package kr.hhplus.be.server.concert_category.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.concert.service.ConcertService;
import kr.hhplus.be.server.concert_category.dto.ConcertWithCategoryResponse;
import kr.hhplus.be.server.category.entity.Category;
import kr.hhplus.be.server.concert.entity.Concert;
import kr.hhplus.be.server.category.repository.CategoryRepository;
import kr.hhplus.be.server.concert_category.repository.ConcertCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ConcertCategoryService {

    private final ConcertService concertService;

    private final ConcertCategoryRepository concertCategoryRepository;

    public ConcertWithCategoryResponse getConcertWithCategories(Long concertId) {
        Concert concert = concertService.getOrThrow(concertId);

        Map<Long, String> categories = new HashMap<>();
        concertCategoryRepository.findById_concertId(concertId)
                .forEach(concertCategory -> {
                    Category category = concertCategory.getCategory();
                    categories.put(category.getId(), category.getName());
                });

        return ConcertWithCategoryResponse.builder()
                .id(concert.getId())
                .name(concert.getName())
                .categories(categories)
                .date(concert.getDate())
                .venue(concert.getVenue())
                .createdAt(concert.getCreatedAt())
                .updatedAt(concert.getUpdatedAt())
                .build();
    }


}
