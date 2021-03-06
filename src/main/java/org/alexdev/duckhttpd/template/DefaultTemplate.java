package org.alexdev.duckhttpd.template;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.response.ResponseBuilder;

public class DefaultTemplate extends Template {
    private FullHttpResponse response;
    public DefaultTemplate(WebConnection webSession) {
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
    public Object get(String key) {
        return null;
    }

    @Override
    public void render() {
        this.webConnection.send(ResponseBuilder.create("Hook into this using a template library!"));
    }
}
