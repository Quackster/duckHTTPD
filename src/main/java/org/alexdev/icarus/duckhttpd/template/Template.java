package org.alexdev.icarus.duckhttpd.template;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.icarus.duckhttpd.server.session.WebSession;

public abstract class Template {

    protected final WebSession webSession;

    public Template(WebSession webSession) {
        this.webSession = webSession;
    }

    public abstract void start(String templateFile) throws Exception;
    public abstract void set(String key, Object value);
    public abstract FullHttpResponse render();
}
