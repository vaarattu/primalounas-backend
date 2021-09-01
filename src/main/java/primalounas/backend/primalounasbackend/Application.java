package primalounas.backend.primalounasbackend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@ComponentScan("primalounas.backend.primalounasbackend")
@EnableScheduling
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		log.info("Application starting...");
		SpringApplication.run(Application.class, args);
	}
}
