package nl.abnamro.assessment.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class CollectionUtilTest {
    @Test
    @DisplayName("getIndex should return the correct index")
    public void getIndex_ShouldReturnCorrectIndex() {
        final var nonUniqueItems = List.of("Item 2", "Item 3", "NON_UNIQUE", "NON_UNIQUE");
        final var index = 2;
        final var hashSet = new LinkedHashSet<String>();
        var result = -1;

        for (final var nonUniqueItem : nonUniqueItems) {
            if (!hashSet.add(nonUniqueItem)) {
                result = CollectionUtil.getIndex(hashSet, nonUniqueItem);
                break;
            }
        }

        Assertions.assertEquals(index, result);
    }
}
