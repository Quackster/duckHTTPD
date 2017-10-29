package org.alexdev.icarus.duckhttpd.server.session;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WebCookies {

    private WebSession session;

    public WebCookies(WebSession session) {
        this.session = session;
    }

    public boolean exists(String name) {
        return get(name) != null;
    }

    public Cookie get(String name) {

        String cookieString = session.request().headers().get(HttpHeaderNames.COOKIE);

        if (cookieString != null && cookieString.length() > 0) {
            Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieString);

            for (Cookie cookie : cookies) {
                if (name.equalsIgnoreCase(cookie.name())){
                    return cookie;
                }
            }
        }

        return null;
    }

    public Cookie set(String name, String value) {

        HttpHeaders httpHeaders = session.response().headers();
        Cookie cookie = new DefaultCookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(-1);
        httpHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie));
        return cookie;
    }

    public Cookie set(String name, String value, int age, TimeUnit unit) {

        HttpHeaders httpHeaders = session.response().headers();
        Cookie cookie = new DefaultCookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(unit.toSeconds(age));

        httpHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie));
        return cookie;
    }
}
