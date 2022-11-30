package nl.abnamro.assessment.api.v1.data;

import java.io.Serializable;

public record InstructionResponse(String detail, int ordinal) implements Serializable {
}
