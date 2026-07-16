package pt.iade.find.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pt.iade.find.model.Bench;
import pt.iade.find.model.BenchStatus;

import java.util.List;

public interface BenchRepository extends JpaRepository<Bench, Long>, JpaSpecificationExecutor<Bench> {
    List<Bench> findByStatus(BenchStatus status);
}
