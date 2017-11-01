package org.alexdev.duckhttpd.response;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;

public class DefaultWebResponse implements WebResponses {

    @Override
    public FullHttpResponse getForbiddenResponse(WebConnection client) {
        return ResponseBuilder.create(HttpResponseStatus.FORBIDDEN, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Forbidden</h1>\n" + "<body>\n" + "</html>");
    }

    @Override
    public FullHttpResponse getNotFoundResponse(WebConnection client) {
        return ResponseBuilder.create(HttpResponseStatus.NOT_FOUND, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Not Found</h1>\n" + "<body>\n" + "</html>");
    }

    @Override
    public FullHttpResponse getInternalServerErrorResponse(WebConnection client, Throwable cause) {
        return ResponseBuilder.create(HttpResponseStatus.INTERNAL_SERVER_ERROR, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Internal Server Error</h1>\n" + "<body>\n" + "</html>");
    }

    @Override
    public FullHttpResponse getErrorResponse(WebConnection client, String header, String message) {
        return ResponseBuilder.create(HttpResponseStatus.INTERNAL_SERVER_ERROR, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>" + header + "</h1>\n</p>" + message + "</p><body>\n" + "</html>");
    }
}
