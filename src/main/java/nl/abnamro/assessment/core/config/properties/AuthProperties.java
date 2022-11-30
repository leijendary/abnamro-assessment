package nl.abnamro.assessment.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private AnonymousUser anonymousUser = new AnonymousUser();

    @Data
    public static class AnonymousUser {
        private String principal;
    }
}
