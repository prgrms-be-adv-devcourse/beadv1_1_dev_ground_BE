package io.devground.dbay;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
public class DbayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbayApplication.class, args);
    }

}
