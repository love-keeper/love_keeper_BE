package com.example.lovekeeper.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");

		return new OpenAPI()
			.info(new Info()
				.title("Love Keeper API")
				.description("LoveKeeper 애플리케이션의 API 문서")
				.version("1.0.0"))
			.components(new Components()
				.addSecuritySchemes("bearerAuth", securityScheme))
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}
}