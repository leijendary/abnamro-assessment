package nl.abnamro.assessment.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Instant;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "dateTimeProvider")
@EnableTransactionManagement
public class JpaConfiguration {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            final var authentication = SecurityContextHolder.getContext().getAuthentication();

            return Optional
                    .ofNullable(authentication)
                    .map(Authentication::getName);
        };
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(Instant.now());
    }
}
