package org.alexdev.duckhttpd.response;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.alexdev.duckhttpd.util.WebUtilities;
import org.alexdev.duckhttpd.util.config.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResponseBuilder {

    public static FullHttpResponse create(String text) {
        return create(HttpResponseStatus.OK, text);
    }

    public static FullHttpResponse create(HttpResponseStatus status, String text) {

        byte[] data = text.getBytes();

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(data)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, data.length);
        return response;
    }

    public static FullHttpResponse create(File file, FullHttpRequest request) throws IOException {

        byte[] fileData = WebUtilities.readFile(file);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(fileData)
        );

        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, WebUtilities.getMimeType(file));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileData.length);
        return response;
    }

    public static FullHttpResponse create(FullHttpRequest request) throws IOException {

        Path path = Paths.get(Settings.getInstance().getSiteDirectory(), request.uri().replace("\\/?", "/?").split("\\?")[0]);
        final File file = path.toFile();

        if (file != null && file.exists()) {
            if (file.isFile()) {
                return ResponseBuilder.create(file, request);
            }

            File indexFile = Paths.get(Settings.getInstance().getSiteDirectory(), request.uri().replace("\\/?", "/?").split("\\?")[0], "index.html").toFile();

            if (indexFile.exists() && indexFile.isFile()) {
                return ResponseBuilder.create(indexFile, request);
            }

            return Settings.getInstance().getResponses().getForbiddenResponse();//ResponseBuilder.create(HttpResponseStatus.FORBIDDEN, WebResponses.getForbiddenText());
        }

        return null;
    }
}
