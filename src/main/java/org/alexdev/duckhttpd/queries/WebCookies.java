package org.alexdev.duckhttpd.queries;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.alexdev.duckhttpd.server.connection.WebConnection;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WebCookies {

    private WebConnection session;
    private List<Cookie> cookieList;

    public WebCookies(WebConnection session) {
        this.session = session;
        this.cookieList = new ArrayList<>();
    }

    public boolean exists(String name) {
        return get(name) != null;
    }

    public String getString(String name) {
        if (this.exists(name)) {
            return get(name).toString();
        }

        return null;
    }

    public String getString(String name, String defaultValue) {
        if (this.exists(name)) {
            return get(name).toString();
        }

        return defaultValue;
    }

    public String get(String name) {

        String cookieString = session.request().headers().get(HttpHeaderNames.COOKIE);

        if (cookieString != null && cookieString.length() > 0) {
            Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieString);

            for (Cookie cookie : cookies) {
                if (name.equalsIgnoreCase(cookie.name())){
                    return cookie.value();
                }
            }
        }

        return null;
    }


    public Cookie set(String name, String value) {
        return set(name, value, 0, null);
    }

    public Cookie set(String name, String value, int age, TimeUnit unit) {

        //HttpHeaders httpHeaders = session.response().headers();
        Cookie cookie = new DefaultCookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);

        if (unit != null) {
            cookie.setMaxAge(unit.toSeconds(age));
        }

        cookieList.add(cookie);

        //httpHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie));
        return cookie;
    }

    public void encodeCookies(FullHttpResponse response) {

        HttpHeaders httpHeaders = response.headers();

        for (Cookie cookie : cookieList) {
            httpHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie));
        }

        this.cookieList.clear();
    }
}