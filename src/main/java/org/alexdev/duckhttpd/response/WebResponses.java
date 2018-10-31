package org.alexdev.duckhttpd.response;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.duckhttpd.server.connection.WebConnection;

public interface WebResponses {

    FullHttpResponse getForbiddenResponse(WebConnection client);
    FullHttpResponse getNotFoundResponse(WebConnection client);
    FullHttpResponse getInternalServerErrorResponse(WebConnection client, Throwable cause);
    FullHttpResponse getErrorResponse(WebConnection client, String header, String message);
}
