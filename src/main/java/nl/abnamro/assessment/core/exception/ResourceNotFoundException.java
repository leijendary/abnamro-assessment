package nl.abnamro.assessment.core.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final List<String> source;
    private final Object identifier;

    public ResourceNotFoundException(final List<String> source, final Object identifier) {
        super(String.format("%s with identifier %s not found", source, identifier));

        this.source = source;
        this.identifier = identifier;
    }
}
