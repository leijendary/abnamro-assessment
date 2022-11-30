package nl.abnamro.assessment.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;

@Data
@Embeddable
public class RecipeIngredient {
    private String name;
    private String value;
    private String unit;
    private int ordinal;

    @Getter
    public enum Unit {
        GRAM("g"),
        KILOGRAM("kg"),
        VALUE("value");

        private final String name;

        Unit(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
