package nl.abnamro.assessment.api.v1.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        UUID id,
        String title,
        List<InstructionResponse> instructions,
        String dishType,
        List<IngredientResponse> ingredients,
        int servingSize,
        int version,
        LocalDateTime createdAt,
        String createdBy
) implements Serializable {
}
