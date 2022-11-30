package nl.abnamro.assessment.api.v1.data;

import java.io.Serializable;

public record IngredientResponse(String name, String value, String unit, int ordinal) implements Serializable {
}
