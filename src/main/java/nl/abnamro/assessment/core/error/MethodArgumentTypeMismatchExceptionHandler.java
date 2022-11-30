package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorData;
import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;

@RestControllerAdvice
@Order(3)
public class MethodArgumentTypeMismatchExceptionHandler {
    private final MessageSource messageSource;

    public MethodArgumentTypeMismatchExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse catchMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        final var code = "error.invalid.type";
        final var name = exception.getName();
        final var parameter = exception.getParameter();
        final var requiredType = parameter.getParameterType().getSimpleName();
        final var value = exception.getValue();
        final var valueType = Optional
                .ofNullable(ClassUtils.getDescriptiveType(value))
                .map(ClassUtils::getShortName)
                .orElse(null);
        final var arguments = new Object[]{name, value, valueType, requiredType};
        final var message = messageSource.getMessage(code, arguments, RequestContext.locale());
        final var source = List.of("body", name);
        final var errorData = new ErrorData(source, code, message);

        return ErrorResponse.builder()
                .addError(errorData)
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }
}
