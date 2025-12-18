package io.devground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

// @OpenAPIDefinition(
// 		servers = {
// 				@Server(url = "/user", description = "User service via gateway")
// 		}
// )
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@EnableFeignClients
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
