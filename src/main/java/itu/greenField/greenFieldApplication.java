package itu.greenField;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class greenFieldApplication {

	public static void main(String[] args) {
		SpringApplication.run(greenFieldApplication.class, args);
	}

}
