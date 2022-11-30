package nl.abnamro.assessment.core.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class ReflectionUtil {
    @SneakyThrows
    public static Object get(final Object object, final String property) {
        Field field;
        final var cls = object.getClass();

        try {
            field = cls.getDeclaredField(property);
        } catch (final NoSuchFieldException ignored) {
            field = cls.getSuperclass().getDeclaredField(property);
        }

        field.setAccessible(true);

        return field.get(object);
    }
}
