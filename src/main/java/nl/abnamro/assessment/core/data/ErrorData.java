package nl.abnamro.assessment.core.data;

import org.springframework.lang.Nullable;

import java.util.List;

public record ErrorData(List<?> source, String code, @Nullable String message) {
}
