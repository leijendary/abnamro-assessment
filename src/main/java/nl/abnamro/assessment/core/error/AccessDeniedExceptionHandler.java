package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorData;
import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Order(1)
public class AccessDeniedExceptionHandler {
    private final MessageSource messageSource;

    public AccessDeniedExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse catchAccessDenied(final AccessDeniedException exception) {
        final var code = "access.denied";
        final var message = messageSource.getMessage(code, new Object[0], RequestContext.locale());
        final var source = List.of("header", "authorization");
        final var errorData = new ErrorData(source, code, "%s: %s".formatted(message, exception.getMessage()));

        return ErrorResponse.builder()
                .addError(errorData)
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}
