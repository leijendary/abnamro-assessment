package nl.abnamro.assessment.core.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class LinkUtil {
    public static HashMap<String, URI> links(final Page<?> page) {
        final var links = new HashMap<String, URI>();
        final var size = page.getSize();
        final var sort = page.getSort();
        final var self = createLink(page.getPageable().getPageNumber(), size, sort);

        links.put("self", self);

        if (page.hasPrevious()) {
            final var previousPageable = page.previousOrFirstPageable();
            final var previous = createLink(previousPageable.getPageNumber(), size, sort);

            links.put("previous", previous);
        }

        if (page.hasNext()) {
            final var nextPageable = page.nextOrLastPageable();
            final var next = createLink(nextPageable.getPageNumber(), size, sort);

            links.put("next", next);
        }

        final var totalPages = page.getTotalPages();
        final var last = createLink(totalPages > 0 ? totalPages - 1 : totalPages, size, sort);

        links.put("last", last);

        return links;
    }

    private static URI createLink(final int page, final int size, final Sort sort) {
        final var uri = RequestContext.uri();

        if (uri == null) {
            return null;
        }

        final var builder = UriComponentsBuilder.fromUri(uri)
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size);

        if (sort.isSorted()) {
            final var set = sort.toSet();
            final var sortParams = new ArrayList<String>();

            for (final var order : set) {
                sortParams.add("%s,%s".formatted(order.getProperty(), order.getDirection()));
            }

            builder.replaceQueryParam("sort", sortParams);
        }

        return builder.build().toUri();
    }
}
