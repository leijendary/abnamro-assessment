package nl.abnamro.assessment.api.v1.data;

import jakarta.validation.constraints.Min;

import java.util.List;

public record RecipeFilterRequest(
        String dishType,

        @Min(value = 1, message = "validation.min")
        Integer servingSize,

        List<String> ingredients
) {
}