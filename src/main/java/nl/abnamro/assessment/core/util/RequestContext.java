package nl.abnamro.assessment.core.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.Locale;

public class RequestContext {
    @Nullable
    public static HttpServletRequest currentRequest() {
        final var attributes = RequestContextHolder.getRequestAttributes();

        if (attributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) attributes).getRequest();
        }

        return null;
    }

    public static String userId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Nullable
    public static URI uri() {
        final var request = currentRequest();

        if (request == null) {
            return null;
        }

        final var contextPath = request.getContextPath();
        final var queryString = request.getQueryString();
        var url = request.getRequestURI().replaceFirst(contextPath, "");

        if (queryString != null) {
            url = "%s?%s".formatted(url, queryString);
        }

        return URI.create(url);
    }

    public static Locale locale() {
        return LocaleContextHolder.getLocale();
    }
}
