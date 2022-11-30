package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorData;
import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.util.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
@Order(3)
public class DataIntegrityViolationExceptionHandler {
    private final MessageSource messageSource;

    public DataIntegrityViolationExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> catchDataIntegrityViolation(final DataIntegrityViolationException exception) {
        final var errorResponse = getResponse(exception);
        final var status = (int) errorResponse.getMeta().get("status");

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

    private ErrorResponse getResponse(final DataIntegrityViolationException exception) {
        final var cause = exception.getCause();

        if (cause instanceof ConstraintViolationException constraintViolationException) {
            return constraintViolationException(constraintViolationException);
        }

        final var code = "error.data.integrity";
        final var arguments = new String[]{Optional.ofNullable(exception.getMessage()).orElse("")};
        final var message = messageSource.getMessage(code, arguments, RequestContext.locale());
        final var source = Arrays.asList("data", "entity");
        final var errorData = new ErrorData(source, code, message);

        return ErrorResponse.builder()
                .addError(errorData)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    private ErrorResponse constraintViolationException(final ConstraintViolationException exception) {
        final var errorMessage = exception.getSQLException().getMessage();
        var field = StringUtils.substringBetween(errorMessage, "Key (", ")=");
        field = StringUtils.substringBetween(field, "(", "::");
        field = CaseUtils.toCamelCase(field, false);

        var table = StringUtils.substringBetween(
                errorMessage,
                "constraint \"",
                "_%s_key".formatted(field)
        );
        table = CaseUtils.toCamelCase(table, true);

        final var value = StringUtils.substringBetween(errorMessage, "=(", ") ");
        final var code = "validation.alreadyExists";
        final var arguments = new String[]{field, value};
        final var message = messageSource.getMessage(code, arguments, RequestContext.locale());
        final var source = List.of("data", table, field);
        final var errorData = new ErrorData(source, code, message);

        return ErrorResponse.builder()
                .addError(errorData)
                .status(HttpStatus.CONFLICT)
                .build();
    }
}
