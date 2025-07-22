package top.huzz.resilix.example.quickstart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenji
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "top.huzz.resilix")
public class ResilixApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResilixApplication.class, args);
    }
}
