package nl.abnamro.assessment.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private List<ErrorData> errors = new ArrayList<>();
    private Map<String, Object> meta = new HashMap<>();
    private Map<String, URI> links = new HashMap<>();

    public static class ErrorResponseBuilder {
        private final List<ErrorData> errors = new ArrayList<>();
        private final Map<String, Object> meta = new HashMap<>();
        private final Map<String, URI> links = new HashMap<>();

        public ErrorResponse build() {
            final var now = Instant.now().toEpochMilli();

            meta.put("timestamp", now);

            return new ErrorResponse(errors, meta, links);
        }

        public ErrorResponseBuilder addError(final List<?> source, final String code, @Nullable final String message) {
            final var errorData = new ErrorData(source, code, message);

            return addError(errorData);
        }

        public ErrorResponseBuilder addError(final ErrorData errorData) {
            errors.add(errorData);

            return this;
        }

        public ErrorResponseBuilder addErrors(final List<ErrorData> errorDatum) {
            errors.addAll(errorDatum);

            return this;
        }


        public ErrorResponseBuilder status(final HttpStatus httpStatus) {
            meta.put("status", httpStatus.value());

            return this;
        }

        public ErrorResponseBuilder selfLink() {
            links.put("self", RequestContext.uri());

            return this;
        }
    }

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .selfLink();
    }
}
