package nl.abnamro.assessment.core.data;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PageMeta {
    private final int numberOfElements;
    private final int totalPages;
    private final long totalElements;
    private final int size;
    private final int number;

    public PageMeta(final Page<?> page) {
        this.numberOfElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.number = page.getNumber();
    }
}
