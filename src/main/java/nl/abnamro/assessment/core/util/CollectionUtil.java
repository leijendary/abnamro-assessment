package nl.abnamro.assessment.core.util;

import java.util.LinkedHashSet;

public class CollectionUtil {
    public static <T> int getIndex(final LinkedHashSet<T> set, final T value) {
        int result = 0;

        for (final var entry : set) {
            if (entry.equals(value)) {
                return result;
            }

            result++;
        }

        return -1;
    }
}
