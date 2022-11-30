package nl.abnamro.assessment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
@Embeddable
public class RecipeInstruction implements Serializable {
    private String detail;

    @Column(insertable = false, updatable = false)
    private String detailSearch;

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
