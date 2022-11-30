package nl.abnamro.assessment.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.abnamro.assessment.core.validator.annotation.EnumField;

import java.util.ArrayList;
import java.util.List;

public class EnumFieldValidator implements ConstraintValidator<EnumField, String> {
    private final List<String> values = new ArrayList<>();

    @Override
    public void initialize(final EnumField constraintAnnotation) {
        final var enumClass = constraintAnnotation.enumClass();
        final var array = enumClass.getEnumConstants();

        for (final var value : array) {
            values.add(value.toString().toUpperCase());
        }
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return values.contains(value.toUpperCase());
    }
}
