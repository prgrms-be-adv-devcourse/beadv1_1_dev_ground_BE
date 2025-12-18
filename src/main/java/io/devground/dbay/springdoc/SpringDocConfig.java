package io.devground.dbay.springdoc;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "D-Bay API"))
public class SpringDocConfig {

	@Bean
	public GroupedOpenApi groupApi() {
		return GroupedOpenApi.builder()
			.group("api")
			.pathsToMatch("/api/**")
			.build();
	}
}
