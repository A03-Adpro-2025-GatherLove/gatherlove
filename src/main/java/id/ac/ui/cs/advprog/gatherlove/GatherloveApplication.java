package id.ac.ui.cs.advprog.gatherlove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GatherloveApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatherloveApplication.class, args);
	}

}
