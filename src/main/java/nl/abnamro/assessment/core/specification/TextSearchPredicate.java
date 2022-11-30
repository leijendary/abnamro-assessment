package nl.abnamro.assessment.core.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;


public class TextSearchPredicate {
    public static Predicate withQuery(final Path<?> path, final String query, final CriteriaBuilder builder) {
        final var filter = String.join(" | ", query.split(" "));
        final var tsQuery = builder.function(
                "to_tsquery",
                String.class,
                builder.literal(filter)
        );
        final var tsRank = builder
                .function("ts_rank", Double.class, path, tsQuery)
                .as(Double.class);

        return builder.greaterThan(tsRank, 0D);
    }
}
