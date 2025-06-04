package kr.hhplus.be.server.concert_category.service;

import kr.hhplus.be.server.category.entity.Category;
import kr.hhplus.be.server.concert.entity.Concert;
import kr.hhplus.be.server.concert.service.ConcertService;
import kr.hhplus.be.server.concert_category.dto.ConcertWithCategoryResponse;
import kr.hhplus.be.server.concert_category.entity.ConcertCategory;
import kr.hhplus.be.server.concert_category.repository.ConcertCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Mock 주입을 가능하게 해주는 어노테이션(JUnit5 + Mockito)
 */
@ExtendWith(MockitoExtension.class)
class ConcertCategoryServiceTest {

    @Mock
    ConcertService concertService;

    @Mock
    ConcertCategoryRepository concertCategoryRepository;

    @InjectMocks
    ConcertCategoryService concertCategoryService;

    @Test
    void getConcertWithCategories_정상동작() {
        // given (상황 설정)
        Long concertId = 1L;
        Concert concert = Concert.builder()
                .id(concertId)
                .name("Test Concert")
                .date(LocalDateTime.now())
                .venue("Seoul")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Category category = Category.builder()
                .id(2L)
                .name("뮤지컬")
                .build();

        // 실제로 new 하지 않고, Mockito의 mock()으로 만든 객체
        ConcertCategory fakeConcertCategory = mock(ConcertCategory.class);
        when(fakeConcertCategory.getCategory()).thenReturn(category);

        when(concertService.getOrThrow(concertId)).thenReturn(concert);
        when(concertCategoryRepository.findById_concertId(concertId))
                .thenReturn(List.of(fakeConcertCategory));

        // when (행동)
        ConcertWithCategoryResponse result = concertCategoryService.getConcertWithCategories(concertId);

        // then (검증)
        assertEquals("Test Concert", result.getName());
        assertEquals("뮤지컬", result.getCategories().get(2L));
    }
}
