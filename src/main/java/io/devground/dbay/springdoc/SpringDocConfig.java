package io.devground.dbay.springdoc;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configurable
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
