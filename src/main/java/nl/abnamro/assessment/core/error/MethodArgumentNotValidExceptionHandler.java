package nl.abnamro.assessment.core.error;

import nl.abnamro.assessment.core.data.ErrorResponse;
import nl.abnamro.assessment.core.service.ErrorService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(4)
public class MethodArgumentNotValidExceptionHandler {
    private final ErrorService errorService;

    public MethodArgumentNotValidExceptionHandler(final ErrorService errorService) {
        this.errorService = errorService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse catchMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
        final var errorData = errorService.toErrorData(exception, "body");

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .addErrors(errorData)
                .build();
    }
}
