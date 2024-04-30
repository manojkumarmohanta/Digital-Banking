package com.jtbank.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(title = "Digital-Banking",
        description = "The Digital Banking Application is a comprehensive online banking platform designed to provide " +
                "users with convenient and secure access to their bank accounts and financial services. The application " +
                "offers a range of features and functionalities to meet the banking needs of today's digital consumers.",
        summary = "Banking Application",
        termsOfService = "Jyonmishra@2024",
        contact = @Contact(name = "Jyoti Narayan Mishra", email = "jyotinarayanmishra05@gmail.com",
                url = "https://github.com/Jyonmishra750"), version = "Api/V1"))

@SecurityScheme(name = "Authorization",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.APIKEY,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Digital-Banking")
@Configuration
public class SwaggerConfig {
}
