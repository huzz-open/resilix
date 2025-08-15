package top.huzz.jaksho;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import top.huzz.jaksho.api.autoconfigure.BucketAutoconfigure;

/**
 * @author chenji
 * @since 1.0.2
 */
@SpringBootApplication(scanBasePackages = "top.huzz")
@EnableDubbo
@Import(BucketAutoconfigure.class)
public class JakshoApplication {
    public static void main(String[] args) {
        SpringApplication.run(JakshoApplication.class, args);
    }
}
