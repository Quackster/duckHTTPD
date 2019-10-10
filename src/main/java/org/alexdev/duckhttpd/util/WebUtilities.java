package org.alexdev.duckhttpd.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import org.alexdev.duckhttpd.util.config.Settings;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtilities {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    public static String getMimeType(File file) {
        return MimeType.getInstance().getTypes().get(FilenameUtils.getExtension(file.getName()));
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
     *
     * @param ctx
     *            Context
     */
    public static void sendNotModified(Channel ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response
     *            HTTP response
     */
    public static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param file
     *            file to extract content type
     */
    public static void setContentTypeHeader(HttpResponse response, File file) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, getMimeType(file) + "; charset=utf-8");//mimeTypesMap.getContentType(file.getPath()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param fileToCache
     *            file to extract content type
     */
    public static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        int HTTP_CACHE_SECONDS = Settings.getInstance().getCacheRenewTime();

        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Match wildcard and get entries inbetween
     * @param syntax the pattern
     * @param input the input from browser
     * @return the matched strings
     */
    public static List<String> getWildcardEntries(String syntax, String input) {
        List<String> list = new ArrayList<>();

        String regex = "(.*)";

        if (StringUtils.countMatches(syntax, "*") > 1) {
            regex = "(.*?)";
        }

        String compiled = syntax.replace("*", regex);

        // Small fix for matching with regex at the end
        if (compiled.endsWith(regex)) {
            compiled = compiled + "/";

            if (!input.endsWith("/")) {
                input = input + "/";
            }
        }

        Pattern p = Pattern.compile(compiled);
        Matcher m = p.matcher(input);

        while (m.find()) {
            for (int i = 1; i < m.groupCount() + 1; i++) {
                try { list.add(m.group(i)); } catch (Exception ignored) { }
            }
        }

        return list;
    }
}
