package pt.iade.find.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.iade.find.dto.BenchRequest;
import pt.iade.find.dto.BenchResponse;
import pt.iade.find.model.Bench;
import pt.iade.find.model.BenchStatus;
import pt.iade.find.model.BenchType;
import pt.iade.find.model.User;
import pt.iade.find.repository.BenchRepository;
import pt.iade.find.repository.RatingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class BenchService {

    private final BenchRepository benchRepository;
    private final RatingRepository ratingRepository;
    private final CurrentUserService currentUserService;

    public BenchService(
            BenchRepository benchRepository,
            RatingRepository ratingRepository,
            CurrentUserService currentUserService) {
        this.benchRepository = benchRepository;
        this.ratingRepository = ratingRepository;
        this.currentUserService = currentUserService;
    }

    public List<BenchResponse> searchApproved(BenchType type, String color, Double minWidth, Double maxWidth) {
        Specification<Bench> spec = buildSearchSpec(BenchStatus.APPROVED, type, color, minWidth, maxWidth);
        return benchRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BenchResponse> listPending() {
        return benchRepository.findByStatus(BenchStatus.PENDING).stream()
                .map(this::toResponse)
                .toList();
    }

    public BenchResponse getById(Long id) {
        Bench bench = benchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banco não encontrado"));
        return toResponse(bench);
    }

    @Transactional
    public BenchResponse report(BenchRequest request) {
        User user = currentUserService.getCurrentUser();

        Bench bench = new Bench();
        bench.setLatitude(request.getLatitude());
        bench.setLongitude(request.getLongitude());
        bench.setType(request.getType());
        bench.setColor(request.getColor());
        bench.setWidthMeters(request.getWidthMeters());
        bench.setStatus(BenchStatus.PENDING);
        bench.setReportedBy(user);

        return toResponse(benchRepository.save(bench));
    }

    @Transactional
    public BenchResponse approve(Long id) {
        Bench bench = findBench(id);
        bench.setStatus(BenchStatus.APPROVED);
        return toResponse(benchRepository.save(bench));
    }

    @Transactional
    public BenchResponse reject(Long id) {
        Bench bench = findBench(id);
        bench.setStatus(BenchStatus.REJECTED);
        return toResponse(benchRepository.save(bench));
    }

    private Bench findBench(Long id) {
        return benchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banco não encontrado"));
    }

    private BenchResponse toResponse(Bench bench) {
        Double average = ratingRepository.findAverageStarsByBenchId(bench.getId());
        Long count = ratingRepository.countByBenchId(bench.getId());
        return BenchResponse.from(bench, average, count);
    }

    private Specification<Bench> buildSearchSpec(
            BenchStatus status,
            BenchType type,
            String color,
            Double minWidth,
            Double maxWidth) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), status));

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (color != null && !color.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("color")), "%" + color.toLowerCase() + "%"));
            }
            if (minWidth != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("widthMeters"), minWidth));
            }
            if (maxWidth != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("widthMeters"), maxWidth));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
