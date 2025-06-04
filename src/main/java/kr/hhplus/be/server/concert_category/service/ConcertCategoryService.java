package kr.hhplus.be.server.concert_category.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.concert.service.ConcertService;
import kr.hhplus.be.server.concert_category.dto.ConcertWithCategoryResponse;
import kr.hhplus.be.server.category.entity.Category;
import kr.hhplus.be.server.concert.entity.Concert;
import kr.hhplus.be.server.concert_category.dto.ConcertWithCategorySearchCond;
import kr.hhplus.be.server.concert_category.repository.ConcertCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ConcertCategoryService {

    private final ConcertService concertService;

    private final ConcertCategoryRepository concertCategoryRepository;

    @Transactional
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
    @Transactional
    public List<ConcertWithCategoryResponse> search(ConcertWithCategorySearchCond cond) {
        List<Concert> searchedConcerts = concertService.search(cond);
        List<ConcertWithCategoryResponse> responses = new ArrayList<>();

        for (Concert concert : searchedConcerts) {
            ConcertWithCategoryResponse response = this.getConcertWithCategories(concert.getId());

            // 조건이 없는 경우 전부 추가
            if (cond.getCategories() == null || cond.getCategories().isEmpty()) {
                responses.add(response);
                continue;
            }

            // 조건이 있는 경우만 필터링해서 추가
            for (Long categoryId : cond.getCategories().keySet()) {
                if (response.getCategories().containsKey(categoryId)) {
                    responses.add(response);
                    break;
                }
            }
        }

        return responses;
    }

}
