package pt.iade.find.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.iade.find.dto.RatingRequest;
import pt.iade.find.dto.RatingResponse;
import pt.iade.find.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/api/benches/{benchId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<RatingResponse> list(@PathVariable Long benchId) {
        return ratingService.listByBench(benchId);
    }

    @PostMapping
    public ResponseEntity<RatingResponse> rate(
            @PathVariable Long benchId,
            @Valid @RequestBody RatingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.rate(benchId, request));
    }
}
