package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.util.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Order(4)
public class OptimisticLockingExceptionHandler {
    private final MessageSource messageSource;

    public OptimisticLockingExceptionHandler(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse catchOptimisticLockingFailure(final OptimisticLockingFailureException exception) {
        final var errorMessage = exception.getMessage();
        var table = StringUtils.substringBetween(errorMessage, "table [", "].");
        table = CaseUtils.toCamelCase(table, true);

        final var source = List.of("data", table, "version");
        final var code = "error.data.version.conflict";
        final var message = messageSource.getMessage(code, new Object[0], RequestContext.locale());

        return ErrorResponse.builder()
                .addError(source, code, message)
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }
}
