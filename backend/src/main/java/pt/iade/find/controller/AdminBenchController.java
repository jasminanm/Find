package pt.iade.find.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.iade.find.dto.BenchResponse;
import pt.iade.find.service.BenchService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/benches")
public class AdminBenchController {

    private final BenchService benchService;

    public AdminBenchController(BenchService benchService) {
        this.benchService = benchService;
    }

    @GetMapping("/pending")
    public List<BenchResponse> listPending() {
        return benchService.listPending();
    }

    @PutMapping("/{id}/approve")
    public BenchResponse approve(@PathVariable Long id) {
        return benchService.approve(id);
    }

    @PutMapping("/{id}/reject")
    public BenchResponse reject(@PathVariable Long id) {
        return benchService.reject(id);
    }
}
