package kr.hhplus.be.server.concert_category.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.category.entity.Category;
import kr.hhplus.be.server.concert.entity.Concert;
import kr.hhplus.be.server.concert_category.entity.key.ConcertCategoryId;
import lombok.*;

@Entity
@Table(name = "concert_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConcertCategory {

    @EmbeddedId
    private ConcertCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("concertId")  // EmbeddedId의 필드 이름
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;

}
