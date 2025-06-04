package kr.hhplus.be.server.concert_category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertWithCategoryResponse {

    private Long id;
    private String name;
    private Map<Long, String> categories;
    private LocalDateTime date;
    private String venue;
    private int price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
