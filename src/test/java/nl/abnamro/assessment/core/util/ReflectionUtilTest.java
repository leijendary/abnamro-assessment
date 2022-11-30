package nl.abnamro.assessment.core.util;

import nl.abnamro.assessment.model.Recipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReflectionUtilTest {
    @Test
    @DisplayName("get should return the correct field value")
    public void get_ShouldReturnCorrectValue() {
        final var recipe = new Recipe();
        recipe.setTitle("Title 1");
        recipe.setServingSize(1);

        final var title = ReflectionUtil.get(recipe, "title");
        final var servingSize = ReflectionUtil.get(recipe, "servingSize");

        Assertions.assertEquals(recipe.getTitle(), title);
        Assertions.assertEquals(recipe.getServingSize(), servingSize);
    }
}
