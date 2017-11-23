package org.alexdev.duckhttpd.response;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.MimeType;
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
        return create(status, MimeType.htm, text);
    }

    public static FullHttpResponse create(MimeType mimeType, String text) {
        return create(HttpResponseStatus.OK, mimeType, text);
    }

    public static FullHttpResponse create(HttpResponseStatus status, MimeType mimeType, String text) {

        byte[] data = text.getBytes();

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(data)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeType.contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, data.length);
        return response;
    }


    public static boolean create(File file, WebConnection conn) throws IOException {

        byte[] fileData = WebUtilities.readFile(file);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(fileData)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, WebUtilities.getMimeType(file));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileData.length);
        conn.channel().writeAndFlush(response);

        return true;
    }

    public static boolean create(WebConnection session, FullHttpRequest request) throws IOException {

        Path path = Paths.get(Settings.getInstance().getSiteDirectory(), request.uri().replace("\\/?", "/?").split("\\?")[0]);
        final File file = path.toFile();

        if (file != null && file.exists()) {
            if (file.isFile()) {
                return ResponseBuilder.create(file, session);
            }

            Path indexPath = Paths.get(Settings.getInstance().getSiteDirectory(), request.uri().replace("\\/?", "/?").split("\\?")[0], "index.html");
            File indexFile = indexPath.toFile();

            if (indexFile.exists() && indexFile.isFile()) {
                return ResponseBuilder.create(indexFile, session);
            }

            session.channel().writeAndFlush(Settings.getInstance().getResponses().getForbiddenResponse(session));//ResponseBuilder.create(HttpResponseStatus.FORBIDDEN, WebResponses.getForbiddenText());
            return true;
        }

        return false;
    }
}
