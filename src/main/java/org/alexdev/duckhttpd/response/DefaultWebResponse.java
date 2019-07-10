package org.alexdev.duckhttpd.response;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;

import javax.swing.*;

public class DefaultWebResponse implements WebResponses {

    /*@Override
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
    }*/

    @Override
    public FullHttpResponse getErrorResponse(WebConnection client, Throwable cause) {
        return ResponseBuilder.create(HttpResponseStatus.INTERNAL_SERVER_ERROR, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Internal Server Error</h1>\n" + "<body>\n" + "</html>");
    }

    @Override
    public FullHttpResponse getResponse(HttpResponseStatus status, WebConnection client) {
        if (status == HttpResponseStatus.FORBIDDEN) {
            return ResponseBuilder.create(HttpResponseStatus.FORBIDDEN, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Forbidden</h1>\n" + "<body>\n" + "</html>");
        }

        if (status == HttpResponseStatus.NOT_FOUND) {
            return ResponseBuilder.create(HttpResponseStatus.NOT_FOUND, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Not Found</h1>\n" + "<body>\n" + "</html>");
        }

        if (status == HttpResponseStatus.BAD_REQUEST) {
            return ResponseBuilder.create(HttpResponseStatus.BAD_REQUEST, "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Bad Request</h1>\n" + "<body>\n" + "</html>");
        }

        return null;
    }
}
