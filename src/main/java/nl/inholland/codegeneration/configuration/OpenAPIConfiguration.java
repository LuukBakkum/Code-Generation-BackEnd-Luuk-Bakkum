package nl.inholland.codegeneration.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "MEIN BANK REST API", version = "1.0.0",
    contact = @Contact(name = "Groep 2", email = "688208@student.inholland.nl"))
)
@SecurityScheme(
    name = "bearerToken",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER,
    description = "JWT token to access the main functions of the API"
)
public class OpenAPIConfiguration { }
