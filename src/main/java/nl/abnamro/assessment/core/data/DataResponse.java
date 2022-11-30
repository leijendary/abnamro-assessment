package nl.abnamro.assessment.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.abnamro.assessment.core.util.LinkUtil;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataResponse<T> {
    private T data;
    private Map<String, Object> meta = new HashMap<>();
    private Map<String, URI> links = new HashMap<>();

    public static class DataResponseBuilder<T> {
        private T data;
        private final Map<String, Object> meta = new HashMap<>();
        private final Map<String, URI> links = new HashMap<>();

        public DataResponse<T> build() {
            final var now = Instant.now().toEpochMilli();

            meta.put("timestamp", now);

            return new DataResponse<>(data, meta, links);
        }

        public DataResponseBuilder<T> data(final T data) {
            this.data = data;

            return this;
        }

        public DataResponseBuilder<T> status(final HttpStatus httpStatus) {
            return status(httpStatus.value());
        }

        public DataResponseBuilder<T> status(final int status) {
            meta.put("status", status);

            return this;
        }

        public DataResponseBuilder<T> meta(final Page<?> page) {
            meta.put("page", new PageMeta(page));

            return this;
        }

        public DataResponseBuilder<T> selfLink() {
            links.put("self", RequestContext.uri());

            return this;
        }

        public DataResponseBuilder<T> links(final Page<?> page) {
            final var links = LinkUtil.links(page);

            this.links.putAll(links);

            return this;
        }
    }

    public static <T> DataResponseBuilder<T> builder() {
        return new DataResponseBuilder<T>()
                .status(HttpStatus.OK)
                .selfLink();
    }
}
