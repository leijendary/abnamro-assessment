package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.exception.ResourceNotFoundException;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(3)
public class ResourceNotFoundExceptionHandler {
    private final MessageSource messageSource;

    public ResourceNotFoundExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse catchResourceNotFound(final ResourceNotFoundException exception) {
        final var source = exception.getSource();
        final var code = "error.resource.notFound";
        final var arguments = new Object[]{
                String.join(".", source),
                exception.getIdentifier()
        };
        final var message = messageSource.getMessage(code, arguments, RequestContext.locale());

        return ErrorResponse.builder()
                .addError(source, code, message)
                .status(HttpStatus.NOT_FOUND)
                .build();
    }
}
