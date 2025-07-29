package cz.malickov.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		ApplicationContext app = SpringApplication.run(BackendApplication.class, args);
	}

}
