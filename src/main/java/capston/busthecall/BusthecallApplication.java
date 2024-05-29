package capston.busthecall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusthecallApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusthecallApplication.class, args);
	}

}
