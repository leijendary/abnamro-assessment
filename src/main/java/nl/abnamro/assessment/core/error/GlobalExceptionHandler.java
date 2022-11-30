package nl.abnamro.assessment.core.error;

import lombok.extern.slf4j.Slf4j;
import nl.abnamro.assessment.core.data.ErrorData;
import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Order
@Slf4j
public class GlobalExceptionHandler {
    private final MessageSource messageSource;
    private final Environment environment;

    public GlobalExceptionHandler(final MessageSource messageSource, final Environment environment) {
        this.messageSource = messageSource;
        this.environment = environment;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse catchException(final Exception exception) {
        log.error("Global Exception", exception);

        final var code = "error.serverError";
        final var isProd = environment.acceptsProfiles(Profiles.of("prod"));

        String message;

        if (isProd) {
            message = messageSource.getMessage(code, new String[0], RequestContext.locale());
        } else {
            message = exception.getMessage();
        }

        final var source = Arrays.asList("server", "internal");
        final var errorData = new ErrorData(source, code, message);

        return ErrorResponse.builder()
                .addError(errorData)
                .build();
    }
}
