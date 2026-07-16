package pt.iade.find.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.iade.find.dto.BenchRequest;
import pt.iade.find.dto.BenchResponse;
import pt.iade.find.model.BenchType;
import pt.iade.find.service.BenchService;

import java.util.List;

@RestController
@RequestMapping("/api/benches")
public class BenchController {

    private final BenchService benchService;

    public BenchController(BenchService benchService) {
        this.benchService = benchService;
    }

    @GetMapping
    public List<BenchResponse> search(
            @RequestParam(required = false) BenchType type,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double minWidth,
            @RequestParam(required = false) Double maxWidth) {
        return benchService.searchApproved(type, color, minWidth, maxWidth);
    }

    @GetMapping("/{id}")
    public BenchResponse getById(@PathVariable Long id) {
        return benchService.getById(id);
    }

    @PostMapping
    public ResponseEntity<BenchResponse> report(@Valid @RequestBody BenchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(benchService.report(request));
    }
}
