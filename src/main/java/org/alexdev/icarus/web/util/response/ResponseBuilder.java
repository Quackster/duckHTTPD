package org.alexdev.icarus.web.util.response;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.alexdev.icarus.web.util.WebUtilities;
import org.alexdev.icarus.web.util.config.Settings;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResponseBuilder {

    public static FullHttpResponse getHtmlResponse(String text) {
        return getHtmlResponse(HttpResponseStatus.OK, text);
    }

    public static FullHttpResponse getHtmlResponse(HttpResponseStatus status, String text) {

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

    public static FullHttpResponse getFileResponse(File file, FullHttpRequest request) {

        byte[] fileData = WebUtilities.readFile(file);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(fileData)
        );

        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        }

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, WebUtilities.getMimeType(file));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileData.length);
        return response;
    }

    public static FullHttpResponse handleFileResponse(FullHttpRequest request) {

        Path path = Paths.get(Settings.getInstance().getSiteDirectory(), request.uri().split("\\?")[0]);
        final File file = path.toFile();

        if (file != null && file.exists()) {
            if (file.isFile()) {
                return ResponseBuilder.getFileResponse(file, request);
            }

            File indexFile = Paths.get(Settings.getInstance().getSiteDirectory(), request.uri(), "home.html").toFile();

            if (indexFile.exists() && indexFile.isFile()) {
                return ResponseBuilder.getFileResponse(indexFile, request);
            }

            return Settings.getInstance().getWebResponses().getForbiddenResponse();//ResponseBuilder.getHtmlResponse(HttpResponseStatus.FORBIDDEN, WebResponses.getForbiddenText());
        }

        return null;
    }
}
