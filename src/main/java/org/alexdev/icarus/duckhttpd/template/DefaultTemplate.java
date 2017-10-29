package org.alexdev.icarus.duckhttpd.template;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.icarus.duckhttpd.server.session.WebSession;
import org.alexdev.icarus.duckhttpd.util.response.ResponseBuilder;

public class DefaultTemplate extends Template {

    private FullHttpResponse response;

    public DefaultTemplate(WebSession webSession) {
        super(webSession);
    }

    @Override
    public void start(String templateFile) {
        // DON'T DO ANYTHING HERE
    }

    @Override
    public void set(String key, Object value) {
        // DON'T DO ANYTHING HERE
    }

    @Override
    public FullHttpResponse render() {
        return ResponseBuilder.getHtmlResponse("Hook into this using a template library!");
    }
}
