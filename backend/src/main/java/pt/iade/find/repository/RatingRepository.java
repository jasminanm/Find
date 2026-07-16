package pt.iade.find.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.iade.find.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByBenchId(Long benchId);
    Optional<Rating> findByBenchIdAndUserId(Long benchId, Long userId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.bench.id = :benchId")
    Double findAverageStarsByBenchId(@Param("benchId") Long benchId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.bench.id = :benchId")
    Long countByBenchId(@Param("benchId") Long benchId);
}
