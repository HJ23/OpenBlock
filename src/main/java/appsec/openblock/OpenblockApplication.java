package appsec.openblock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OpenblockApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenblockApplication.class, args);
	}

}
