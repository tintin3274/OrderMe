package th.ku.orderme.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Stream;

public class CookieUtil {
    public static Cookie createCookie(String name, String value, int expiry, boolean isSecure, boolean httpOnly, String path, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(expiry);
        cookie.setSecure(isSecure);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath(path);
        cookie.setDomain(domain);
        return cookie;
    }

    public static Optional<Cookie> readCookie(HttpServletRequest request, String cookieName) {
        return Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst();
    }
}
