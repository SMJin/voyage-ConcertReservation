package kr.hhplus.be.server.concert_category.repository;

import kr.hhplus.be.server.concert_category.entity.ConcertCategory;
import kr.hhplus.be.server.concert_category.entity.key.ConcertCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertCategoryRepository extends JpaRepository<ConcertCategory, ConcertCategoryId> {
    List<ConcertCategory> findById_concertId(Long concertId);
}
