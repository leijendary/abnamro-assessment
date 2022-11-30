package nl.abnamro.assessment.core.config.properties;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "info")
public class InfoProperties {
    private App app = new App();
    private Api api = new Api();

    @Data
    public static class App {
        private String organization;
        private String group;
        private String name;
        private String description;
        private String version;
    }

    @Data
    public static class Api {
        private String termsOfService;
        private Contact contact;
        private License license;
        private Map<String, Object> extensions = new HashMap<>();
    }
}
