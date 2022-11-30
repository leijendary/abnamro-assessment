package nl.abnamro.assessment.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import nl.abnamro.assessment.api.v1.data.RecipeFilterRequest;
import nl.abnamro.assessment.core.data.QueryRequest;
import nl.abnamro.assessment.core.specification.RuleOutPredicate;
import nl.abnamro.assessment.core.specification.TextSearchPredicate;
import nl.abnamro.assessment.model.Recipe;
import nl.abnamro.assessment.model.RecipeIngredient;
import nl.abnamro.assessment.model.RecipeInstruction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public record RecipeSpecification(QueryRequest queryRequest, RecipeFilterRequest recipeFilterRequest)
        implements Specification<Recipe> {
    @Override
    public Predicate toPredicate(
            @NonNull final Root<Recipe> root,
            @NonNull final CriteriaQuery<?> query,
            @NonNull final CriteriaBuilder criteriaBuilder
    ) {
        final var predicates = Arrays.asList(
                withQuery(root, criteriaBuilder),
                withDishType(root, criteriaBuilder),
                withServingSize(root, criteriaBuilder),
                withIngredients(root, criteriaBuilder)
        );
        final var nonNulls = predicates.stream()
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);

        return criteriaBuilder.and(nonNulls);
    }

    @Nullable
    private Predicate withQuery(final Root<Recipe> root, final CriteriaBuilder criteriaBuilder) {
        final var query = queryRequest.query();

        if (StringUtils.isEmpty(query)) {
            return null;
        }

        final var recipeTitlePath = root.<String>get("titleSearch");
        final var titlePredicate = TextSearchPredicate.withQuery(recipeTitlePath, query, criteriaBuilder);

        final var instructionDetailPath = root
                .<RecipeInstruction>get("instructions")
                .<String>get("detailSearch");
        final var instructionDetailPredicate = TextSearchPredicate
                .withQuery(instructionDetailPath, query, criteriaBuilder);

        return criteriaBuilder.or(titlePredicate, instructionDetailPredicate);
    }

    @Nullable
    private Predicate withDishType(final Root<Recipe> root, final CriteriaBuilder criteriaBuilder) {
        final var dishType = recipeFilterRequest.dishType();

        if (StringUtils.isEmpty(dishType)) {
            return null;
        }

        final var path = root.<String>get("dishType");

        return criteriaBuilder.equal(path, dishType);
    }

    @Nullable
    private Predicate withServingSize(final Root<Recipe> root, final CriteriaBuilder criteriaBuilder) {
        final var servingSize = recipeFilterRequest.servingSize();

        if (servingSize == null) {
            return null;
        }

        final var path = root.<Integer>get("servingSize");

        return criteriaBuilder.equal(path, servingSize);
    }

    @Nullable
    private Predicate withIngredients(final Root<Recipe> root, final CriteriaBuilder criteriaBuilder) {
        final var ingredient = Optional
                .ofNullable(recipeFilterRequest.ingredients())
                .orElse(new ArrayList<>());
        final var path = root.<RecipeIngredient>get("ingredients").<String>get("name");

        return RuleOutPredicate.withFilter(path, ingredient, criteriaBuilder);
    }
}
