package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.service.ErrorService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(5)
public class BindExceptionHandler {
    private final ErrorService errorService;

    public BindExceptionHandler(final ErrorService errorService) {
        this.errorService = errorService;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse catchBind(final BindException exception) {
        final var errorData = errorService.toErrorData(exception, "param");

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .addErrors(errorData)
                .build();
    }
}
