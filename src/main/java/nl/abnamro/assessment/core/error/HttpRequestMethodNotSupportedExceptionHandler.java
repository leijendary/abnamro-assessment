package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorData;
import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Order(4)
public class HttpRequestMethodNotSupportedExceptionHandler {
    private final MessageSource messageSource;

    public HttpRequestMethodNotSupportedExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse catchHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException exception) {
        final var source = List.of("request", "method");
        final var code = "error.method.notSupported";
        final var arguments = new Object[]{
                exception.getMethod(),
                exception.getSupportedHttpMethods()
        };
        final var message = messageSource.getMessage(code, arguments, RequestContext.locale());
        final var errorData = new ErrorData(source, code, message);

        return ErrorResponse.builder()
                .addError(errorData)
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .build();
    }
}
