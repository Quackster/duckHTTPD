package org.alexdev.duckhttpd.template;

import org.alexdev.duckhttpd.server.connection.WebConnection;

public abstract class Template {

    protected final WebConnection webConnection;

    public Template(WebConnection webConnection) {
        this.webConnection = webConnection;
    }

    public abstract void start(String templateFile) throws Exception;
    public abstract void set(String key, Object value);
    public abstract void render();
}
