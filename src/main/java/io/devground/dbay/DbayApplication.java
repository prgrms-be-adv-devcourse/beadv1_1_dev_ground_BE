package io.devground.dbay;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
		servers = {
				@Server(url = "/dbay", description = "Dbay service via gateway")
		}
)
@EnableAsync
@EnableKafka
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.devground.dbay")
@EnableElasticsearchRepositories(basePackages = "io.devground.dbay.domain.product.product.repository")
public class DbayApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbayApplication.class, args);
	}

}
