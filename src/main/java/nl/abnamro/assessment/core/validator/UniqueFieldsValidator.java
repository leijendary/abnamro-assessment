package nl.abnamro.assessment.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.abnamro.assessment.core.util.CollectionUtil;
import nl.abnamro.assessment.core.util.ReflectionUtil;
import nl.abnamro.assessment.core.validator.annotation.UniqueFields;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class UniqueFieldsValidator implements ConstraintValidator<UniqueFields, List<?>> {
    private String[] uniqueFields;
    private String message;

    @Override
    public void initialize(final UniqueFields constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.uniqueFields = constraintAnnotation.uniqueFields();
    }

    @Override
    public boolean isValid(final List<?> list, final ConstraintValidatorContext context) {
        if (list == null || list.isEmpty()) {
            return true;
        }

        final var fieldSets = new HashMap<String, LinkedHashSet<Object>>();
        var hasDuplicate = false;

        context.disableDefaultConstraintViolation();

        for (final var target : list) {
            for (var i = 0; i < uniqueFields.length; i++) {
                final var field = uniqueFields[i];
                final var value = ReflectionUtil.get(target, field);

                if (value == null) {
                    continue;
                }

                final var set = fieldSets.getOrDefault(field, new LinkedHashSet<>());

                if (!set.add(value)) {
                    final var existingIndex = CollectionUtil.getIndex(set, value);

                    addViolation(existingIndex, context, field);
                    addViolation(i, context, field);

                    hasDuplicate = true;
                }

                fieldSets.put(field, set);
            }
        }

        return !hasDuplicate;
    }

    private void addViolation(int index, ConstraintValidatorContext context, String field) {
        context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .inIterable()
                .atIndex(index)
                .addConstraintViolation();
    }
}
