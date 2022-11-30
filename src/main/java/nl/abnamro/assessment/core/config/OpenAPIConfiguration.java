package nl.abnamro.assessment.core.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import nl.abnamro.assessment.core.config.properties.InfoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = OpenAPIConfiguration.SECURITY_SCHEME_BEARER,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenAPIConfiguration {
    public static final String SECURITY_SCHEME_BEARER = "Bearer";

    private final InfoProperties infoProperties;

    public OpenAPIConfiguration(final InfoProperties infoProperties) {
        this.infoProperties = infoProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(info());
    }

    private Info info() {
        final var app = infoProperties.getApp();
        final var api = infoProperties.getApi();

        return new Info()
                .title(app.getName())
                .description(app.getDescription())
                .termsOfService(api.getTermsOfService())
                .contact(api.getContact())
                .license(api.getLicense())
                .version(app.getVersion());
    }
}
