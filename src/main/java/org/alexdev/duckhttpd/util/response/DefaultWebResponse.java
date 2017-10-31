package org.alexdev.duckhttpd.util.response;

import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultWebResponse implements WebResponses {

    @Override
    public FullHttpResponse getForbiddenResponse() {
        return ResponseBuilder.getHtmlResponse("\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Forbidden</h1>\n" + "<body>\n" + "</html>");
    }

    @Override
    public FullHttpResponse getNotFoundResponse() {
        return ResponseBuilder.getHtmlResponse("\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Not Found</h1>\n" + "<body>\n" + "</html>");
    }

    @Override
    public FullHttpResponse getInternalServerErrorResponse(Throwable cause) {
        return ResponseBuilder.getHtmlResponse("\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Internal Server Error</h1>\n" + "<body>\n" + "</html>");
    }
}
