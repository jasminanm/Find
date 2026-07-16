package pt.iade.find.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pt.iade.find.model.Bench;
import pt.iade.find.model.BenchStatus;
import pt.iade.find.model.BenchType;
import pt.iade.find.model.Role;
import pt.iade.find.model.User;
import pt.iade.find.repository.BenchRepository;
import pt.iade.find.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BenchRepository benchRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            BenchRepository benchRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.benchRepository = benchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = userRepository.findByEmail("admin@find.pt").orElseGet(() -> {
            User user = new User();
            user.setEmail("admin@find.pt");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRole(Role.ADMIN);
            return userRepository.save(user);
        });

        User demoUser = userRepository.findByEmail("user@find.pt").orElseGet(() -> {
            User user = new User();
            user.setEmail("user@find.pt");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.USER);
            return userRepository.save(user);
        });

        if (benchRepository.count() == 0) {
            benchRepository.save(createBench(38.7223, -9.1393, BenchType.WOOD, "Castanho", 1.8, BenchStatus.APPROVED, demoUser));
            benchRepository.save(createBench(38.7250, -9.1500, BenchType.CEMENT, "Cinza", 2.0, BenchStatus.APPROVED, demoUser));
            benchRepository.save(createBench(38.7300, -9.1450, BenchType.METAL, "Preto", 1.5, BenchStatus.PENDING, demoUser));
        }
    }

    private Bench createBench(
            double lat,
            double lng,
            BenchType type,
            String color,
            double width,
            BenchStatus status,
            User reporter) {
        Bench bench = new Bench();
        bench.setLatitude(lat);
        bench.setLongitude(lng);
        bench.setType(type);
        bench.setColor(color);
        bench.setWidthMeters(width);
        bench.setStatus(status);
        bench.setReportedBy(reporter);
        return bench;
    }
}
