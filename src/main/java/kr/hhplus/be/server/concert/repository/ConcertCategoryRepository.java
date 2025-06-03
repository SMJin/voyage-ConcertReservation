package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.concert.entity.ConcertCategory;
import kr.hhplus.be.server.concert.entity.key.ConcertCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertCategoryRepository extends JpaRepository<ConcertCategory, ConcertCategoryId> {
}
