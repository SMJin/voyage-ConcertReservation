package kr.hhplus.be.server.concert_category.controller;

import kr.hhplus.be.server.common.response.success.ApiResponse;
import kr.hhplus.be.server.concert_category.dto.ConcertWithCategoryResponse;
import kr.hhplus.be.server.concert_category.service.ConcertCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/concert-categories")
@RequiredArgsConstructor
public class ConcertCategoryController {

    private final ConcertCategoryService concertCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<ConcertWithCategoryResponse>> getConcertWithCategories(
            @RequestParam("concertId") Long concertId) {
        log.info("getConcertWithCategories concertId={}", concertId);
        ConcertWithCategoryResponse response = concertCategoryService.getConcertWithCategories(concertId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
