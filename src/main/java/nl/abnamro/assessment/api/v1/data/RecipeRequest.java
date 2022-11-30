package nl.abnamro.assessment.api.v1.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import nl.abnamro.assessment.core.validator.annotation.EnumField;
import nl.abnamro.assessment.core.validator.annotation.UniqueFields;
import nl.abnamro.assessment.model.Recipe;

import java.util.List;

public record RecipeRequest(
        @NotBlank(message = "validation.required")
        @Size(max = 200, message = "validation.maxLength")
        String title,

        @NotEmpty(message = "validation.required")
        @UniqueFields(uniqueFields = {"detail", "ordinal"})
        @Valid
        List<InstructionRequest> instructions,

        @NotBlank(message = "validation.required")
        @EnumField(enumClass = Recipe.DishType.class)
        String dishType,

        @NotEmpty(message = "validation.required")
        @UniqueFields(uniqueFields = {"name", "ordinal"})
        @Valid
        List<IngredientRequest> ingredients,

        @Min(value = 1, message = "validation.min")
        @Max(value = 999999, message = "validation.max")
        int servingSize
) {
}
