package nl.abnamro.assessment.core.service;

import nl.abnamro.assessment.core.data.ErrorData;
import nl.abnamro.assessment.core.util.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ErrorService {
    private final MessageSource messageSource;

    public ErrorService(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public List<ErrorData> toErrorData(final BindException exception, final String firstSource) {
        String objectName;
        List<Object> source;
        String[] objectNames;
        String code;
        Object[] arguments;
        String message;
        ErrorData errorData;
        final var result = new ArrayList<ErrorData>();

        for (final var error : exception.getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                objectName = fieldError.getField();
            } else {
                objectName = error.getObjectName();
            }

            source = new ArrayList<>();
            source.add(firstSource);

            objectNames = objectName.split("\\.");

            for (var s : objectNames) {
                final var indexSubstring = StringUtils.substringBetween(s, "[", "]");
                s = StringUtils.substringBefore(s, "[");

                source.add(s);

                if (StringUtils.isNotEmpty(indexSubstring)) {
                    final var index = Integer.parseInt(indexSubstring);

                    source.add(index);
                }
            }

            if (error.contains(TypeMismatchException.class)) {
                final var typeMismatch = error.unwrap(TypeMismatchException.class);

                code = "error.invalid.type";
                arguments = getArguments(typeMismatch);
            } else {
                code = Optional
                        .ofNullable(error.getDefaultMessage())
                        .orElse("");
                arguments = error.getArguments();
            }

            message = messageSource.getMessage(code, arguments, RequestContext.locale());
            errorData = new ErrorData(source, code, message);

            result.add(errorData);
        }

        return result;
    }

    private Object[] getArguments(final TypeMismatchException exception) {
        final var name = exception.getPropertyName();
        final var requiredType = Optional.ofNullable(exception.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("String");
        final var value = exception.getValue();
        final var valueType = Optional
                .ofNullable(ClassUtils.getDescriptiveType(value))
                .map(ClassUtils::getShortName)
                .orElse(null);

        return new Object[]{name, value, valueType, requiredType};
    }
}
