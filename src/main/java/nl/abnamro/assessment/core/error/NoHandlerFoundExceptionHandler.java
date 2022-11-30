package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorData;
import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
@Order(1)
public class NoHandlerFoundExceptionHandler {
    private final MessageSource messageSource;

    public NoHandlerFoundExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse catchNoHandlerFound(final NoHandlerFoundException exception) {
        final var code = "error.mapping.notFound";
        final var arguments = new Object[]{exception.getMessage()};
        final var message = messageSource.getMessage(code, arguments, RequestContext.locale());
        final var source = List.of("request", "path");
        final var errorData = new ErrorData(source, code, message);

        return ErrorResponse.builder()
                .addError(errorData)
                .status(HttpStatus.NOT_FOUND)
                .build();
    }
}
