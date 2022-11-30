package nl.abnamro.assessment.api.v1.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import nl.abnamro.assessment.core.validator.annotation.EnumField;
import nl.abnamro.assessment.model.RecipeIngredient;

public record IngredientRequest(
        @NotBlank(message = "validation.required")
        @Size(max = 200, message = "validation.maxLength")
        String name,

        @NotBlank(message = "validation.required")
        @Size(max = 20, message = "validation.maxLength")
        String value,

        @NotBlank(message = "validation.required")
        @EnumField(enumClass = RecipeIngredient.Unit.class)
        String unit,

        @Min(value = 1, message = "validation.min")
        int ordinal
) {
}
