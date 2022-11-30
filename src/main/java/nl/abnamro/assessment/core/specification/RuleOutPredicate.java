package nl.abnamro.assessment.core.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RuleOutPredicate {
    public static Predicate withFilter(
            final Path<String> path,
            final List<String> filters,
            final CriteriaBuilder builder
    ) {
        final var includes = filters
                .stream()
                .filter(s -> !s.startsWith("!"))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        final var excludes = filters
                .stream()
                .filter(s -> s.startsWith("!"))
                .map(s -> s.replaceFirst("!", "").toLowerCase())
                .collect(Collectors.toList());
        final var predicates = new ArrayList<Predicate>();
        final var expression = builder.lower(path);

        if (!includes.isEmpty()) {
            final var in = expression.in(includes);

            predicates.add(in);
        }

        if (!excludes.isEmpty()) {
            final var notIn = builder.not(expression.in(excludes));

            predicates.add(notIn);
        }

        if (predicates.isEmpty()) {
            return null;
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
