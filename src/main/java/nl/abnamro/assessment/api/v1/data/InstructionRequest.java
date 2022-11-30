package nl.abnamro.assessment.api.v1.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InstructionRequest(
        @NotBlank(message = "validation.required")
        String detail,

        @Min(value = 1, message = "validation.min")
        int ordinal
) {
}
