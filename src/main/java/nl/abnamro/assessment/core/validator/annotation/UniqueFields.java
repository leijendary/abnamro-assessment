package nl.abnamro.assessment.core.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.abnamro.assessment.core.validator.UniqueFieldsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueFieldsValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueFields {
    String message() default "validation.duplicateValue";

    String[] uniqueFields() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
