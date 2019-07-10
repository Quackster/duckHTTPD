package org.alexdev.duckhttpd.response;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;

public interface WebResponses {
    /*FullHttpResponse getForbiddenResponse(WebConnection client);
    FullHttpResponse getNotFoundResponse(WebConnection client);
    FullHttpResponse getInternalServerErrorResponse(WebConnection client, Throwable cause);*/
    public FullHttpResponse getErrorResponse(WebConnection client, Throwable ex);
    public FullHttpResponse getResponse(HttpResponseStatus status, WebConnection client);
}
