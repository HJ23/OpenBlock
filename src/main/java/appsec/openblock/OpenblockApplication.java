package appsec.openblock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

// jquery version 3.4.1 used in some pages of application vulnerable.
// CVE-2020-11022/CVE-2020-11023

@SpringBootApplication
@EnableScheduling
public class OpenblockApplication {
	public static void main(String[] args) {
		SpringApplication.run(OpenblockApplication.class, args);
	}

}
