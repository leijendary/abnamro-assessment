package nl.abnamro.assessment.core.config;

import nl.abnamro.assessment.core.config.properties.AuthProperties;
import nl.abnamro.assessment.core.config.properties.InfoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AuthProperties.class,
        InfoProperties.class
})
public class PropertiesConfiguration {
}
