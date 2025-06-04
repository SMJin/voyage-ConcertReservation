package kr.hhplus.be.server.concert_category.dto;

import kr.hhplus.be.server.concert.dto.ConcertSearchCond;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class ConcertWithCategorySearchCond extends ConcertSearchCond {
    Map<Long, String> categories;
}
