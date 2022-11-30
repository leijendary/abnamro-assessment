package nl.abnamro.assessment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.abnamro.assessment.core.model.AuditingUUIDModel;
import nl.abnamro.assessment.core.model.SoftDeleteModel;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Where(clause = "deleted_at is null")
public class Recipe extends AuditingUUIDModel implements SoftDeleteModel {
    private String title;

    @Column(insertable = false, updatable = false)
    private String titleSearch;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_instruction", joinColumns = @JoinColumn(name = "id"))
    private Set<RecipeInstruction> instructions = new HashSet<>();

    private String dishType;
    private int servingSize;
    private LocalDateTime deletedAt;
    private String deletedBy;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_ingredient", joinColumns = @JoinColumn(name = "id"))
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    @Override
    public void setDeletedAt(final LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public LocalDateTime getDeletedAt() {
        return this.deletedAt;
    }

    @Override
    public void setDeletedBy(final String deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public String getDeletedBy() {
        return this.deletedBy;
    }

    @Getter
    public enum DishType {
        VEGETARIAN("vegetarian"),
        NON_VEGETARIAN("non-vegetarian");

        private final String value;

        DishType(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
