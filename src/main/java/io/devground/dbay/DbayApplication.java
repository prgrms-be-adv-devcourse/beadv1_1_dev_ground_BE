package io.devground.dbay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableKafka
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class DbayApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbayApplication.class, args);
	}

}
