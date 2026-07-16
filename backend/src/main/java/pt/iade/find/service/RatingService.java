package pt.iade.find.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.iade.find.dto.RatingRequest;
import pt.iade.find.dto.RatingResponse;
import pt.iade.find.model.Bench;
import pt.iade.find.model.BenchStatus;
import pt.iade.find.model.Rating;
import pt.iade.find.model.User;
import pt.iade.find.repository.BenchRepository;
import pt.iade.find.repository.RatingRepository;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BenchRepository benchRepository;
    private final CurrentUserService currentUserService;

    public RatingService(
            RatingRepository ratingRepository,
            BenchRepository benchRepository,
            CurrentUserService currentUserService) {
        this.ratingRepository = ratingRepository;
        this.benchRepository = benchRepository;
        this.currentUserService = currentUserService;
    }

    public List<RatingResponse> listByBench(Long benchId) {
        ensureBenchExists(benchId);
        return ratingRepository.findByBenchId(benchId).stream()
                .map(r -> new RatingResponse(
                        r.getId(),
                        r.getStars(),
                        r.getUser().getEmail(),
                        r.getCreatedAt()))
                .toList();
    }

    @Transactional
    public RatingResponse rate(Long benchId, RatingRequest request) {
        Bench bench = benchRepository.findById(benchId)
                .orElseThrow(() -> new IllegalArgumentException("Banco não encontrado"));

        if (bench.getStatus() != BenchStatus.APPROVED) {
            throw new IllegalArgumentException("Só é possível avaliar bancos aprovados");
        }

        User user = currentUserService.getCurrentUser();

        Rating rating = ratingRepository.findByBenchIdAndUserId(benchId, user.getId())
                .orElse(new Rating());

        rating.setBench(bench);
        rating.setUser(user);
        rating.setStars(request.getStars());

        Rating saved = ratingRepository.save(rating);
        return new RatingResponse(
                saved.getId(),
                saved.getStars(),
                saved.getUser().getEmail(),
                saved.getCreatedAt());
    }

    private void ensureBenchExists(Long benchId) {
        if (!benchRepository.existsById(benchId)) {
            throw new IllegalArgumentException("Banco não encontrado");
        }
    }
}
