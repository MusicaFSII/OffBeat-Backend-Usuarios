package com.example.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI().info(new Info()
            .title("Microservicio de Usuarios y Comentarios")
            .description("API REST para el registro, login y administración de usuarios, y para los comentarios del formulario de contacto")
            .version("v1"));
    }
}