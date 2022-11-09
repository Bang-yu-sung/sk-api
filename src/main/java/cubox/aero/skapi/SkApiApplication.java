package cubox.aero.skapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkApiApplication.class, args);
    }
}
