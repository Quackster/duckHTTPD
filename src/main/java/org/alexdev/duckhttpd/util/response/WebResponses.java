package org.alexdev.duckhttpd.util.response;

import io.netty.handler.codec.http.FullHttpResponse;

public interface WebResponses {

    FullHttpResponse getForbiddenResponse();
    FullHttpResponse getNotFoundResponse();
    FullHttpResponse getInternalServerErrorResponse(Throwable cause);

    /*public static String getForbiddenText() {
        return "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Forbidden</h1>\n" + "<body>\n" + "</html>";
    }

    public static String getNotFoundText() {
        return "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Not Found</h1>\n" + "<body>\n" + "</html>";
    }

    public static String getInternalServerError(String errorMessage, String stacktrace) {
        return "\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n" + "   <h1>Internal Server Error</h1>\n<p>" + errorMessage + "</p>\n<p>" + stacktrace + "</p>\n" + "<body>\n" + "</html>";
    }*/
}
