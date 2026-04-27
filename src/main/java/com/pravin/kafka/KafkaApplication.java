package com.pravin.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 http://localhost:8080/api-docs
 http://localhost:8080/swagger-ui/index.html
 http://localhost:8080/actuator/circuitbreakers
 http://localhost:8080/actuator/health
*/


@SpringBootApplication
@EnableScheduling
public class KafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

}
