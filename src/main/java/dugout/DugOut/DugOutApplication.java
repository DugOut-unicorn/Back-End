package dugout.DugOut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DugOutApplication {

	public static void main(String[] args) {
		SpringApplication.run(DugOutApplication.class, args);
	}


}
