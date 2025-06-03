package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.concert.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
