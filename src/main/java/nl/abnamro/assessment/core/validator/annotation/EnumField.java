package nl.abnamro.assessment.core.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.abnamro.assessment.core.validator.EnumFieldValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumFieldValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumField {
    Class<? extends Enum<?>> enumClass();

    String message() default "validation.invalidValue";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
