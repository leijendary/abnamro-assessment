package nl.abnamro.assessment.core.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(2)
public class HttpMessageNotReadableExceptionHandler {
    private final MessageSource messageSource;

    public HttpMessageNotReadableExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse catchHttpMessageNotReadable(final HttpMessageNotReadableException exception) {
        final var builder = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST);

        errors(exception, builder);

        return builder.build();
    }

    private void errors(
            final HttpMessageNotReadableException exception,
            final ErrorResponse.ErrorResponseBuilder builder
    ) {
        final var source = List.of("body");
        final var code = "error.badRequest";
        var message = Optional
                .ofNullable(exception.getMessage())
                .orElse("");

        if (message.startsWith("Required request body is missing")) {
            builder.addError(source, code, message.split(":")[0]);

            return;
        }

        final var cause = exception.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            errors(invalidFormatException, builder);

            return;
        }

        if (cause instanceof JsonMappingException jsonMappingException) {
            errors(jsonMappingException, builder);

            return;
        }

        message = Optional.ofNullable(exception.getMessage())
                .map(s -> s.replace("JSON decoding error: ", ""))
                .orElse("");

        builder.addError(source, code, message);
    }

    private void errors(final InvalidFormatException exception, final ErrorResponse.ErrorResponseBuilder builder) {
        final var source = createSource(exception.getPath());
        final var code = "error.body.format.invalid";
        final var path = source.stream()
                .map(Object::toString)
                .collect(Collectors.joining("."));
        final var arguments = new Object[]{
                path,
                exception.getValue(),
                exception.getTargetType().getSimpleName(),
        };
        final var message = messageSource.getMessage(code, arguments, RequestContext.locale());

        builder.addError(source, code, message);
    }

    private void errors(final JsonMappingException exception, final ErrorResponse.ErrorResponseBuilder builder) {
        final var source = createSource(exception.getPath());
        final var message = exception.getOriginalMessage();

        builder.addError(source, "error.body.format.invalid", message);
    }

    private List<Object> createSource(final List<JsonMappingException.Reference> path) {
        final var source = new ArrayList<Object>(List.of("body"));

        path.forEach(reference -> {
            final var index = reference.getIndex();

            if (index >= 0) {
                source.add(index);
            }

            source.add(reference.getFieldName());
        });

        return source;
    }
}
