package org.alexdev.duckhttpd.response;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.MimeType;
import org.alexdev.duckhttpd.util.WebUtilities;
import org.alexdev.duckhttpd.util.config.Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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


    public static boolean create(File file, WebConnection conn) throws Exception {

        // Cache Validation
        String ifModifiedSince = conn.request().headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(WebUtilities.HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

            // Only compare up to the second because the datetime format we send to the client
            // does not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                WebUtilities.sendNotModified(conn.channel());
                return true;
            }
        }

        RandomAccessFile raf;

        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException ignore) {
            conn.channel().writeAndFlush(Settings.getInstance().getResponses().getNotFoundResponse(conn));
            return true;
        }

        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpUtil.setContentLength(response, fileLength);
        WebUtilities.setContentTypeHeader(response, file);
        WebUtilities.setDateAndCacheHeaders(response, file);

        if (HttpUtil.isKeepAlive(conn.request())) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Write the initial line and the header.
        conn.channel().write(response);

        // Write the content.
        ChannelFuture sendFileFuture;
        ChannelFuture lastContentFuture;

        if (conn.channel().pipeline().get(SslHandler.class) == null) {
            sendFileFuture =
                    conn.channel().write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), conn.channel().newProgressivePromise());
            // Write the end marker.
            lastContentFuture = conn.channel().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            sendFileFuture =
                    conn.channel().writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),
                            conn.channel().newProgressivePromise());
            // HttpChunkedInput will write the end marker (LastHttpContent) for us.
            // Write the end marker.
            lastContentFuture = conn.channel().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }

        // Decide whether to close the connection or not.
        if (!HttpUtil.isKeepAlive(conn.request())) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }

        return true;
    }

    public static boolean create(WebConnection session, FullHttpRequest request) throws Exception {

        String newUri = request.uri().replace("\\/?", "/?").split("\\?")[0];
        Path path = Paths.get(Settings.getInstance().getSiteDirectory(), newUri);

        final File file = path.toFile();

        if (file != null && file.exists()) {

            if (file.isFile()) {
                return ResponseBuilder.create(file, session);
            }

            List<String> indexFiles = Arrays.asList("index.htm", "index.html");

            for (String indexName : indexFiles) {
                Path indexPath = Paths.get(Settings.getInstance().getSiteDirectory(), newUri, indexName);
                File indexFile = indexPath.toFile();

                if (indexFile.exists() && indexFile.isFile()) {
                    return ResponseBuilder.create(indexFile, session);
                }
            }

            session.channel().writeAndFlush(Settings.getInstance().getResponses().getForbiddenResponse(session));//ResponseBuilder.create(HttpResponseStatus.FORBIDDEN, WebResponses.getForbiddenText());
            return true;
        }

        return false;
    }
}
