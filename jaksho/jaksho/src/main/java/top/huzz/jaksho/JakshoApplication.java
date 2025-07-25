package top.huzz.jaksho;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenji
 * @since 1.0.2
 */
@SpringBootApplication
@EnableDubbo
public class JakshoApplication {
    public static void main(String[] args) {
        SpringApplication.run(JakshoApplication.class, args);
    }
}
