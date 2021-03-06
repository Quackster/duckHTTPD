package org.alexdev.duckhttpd.template;

import org.alexdev.duckhttpd.server.connection.WebConnection;

public abstract class Template {

    protected final WebConnection webConnection;

    public Template(WebConnection webConnection) {
        this.webConnection = webConnection;
    }

    public abstract void start(String templateFile);

    public void registerBinder(TemplateBinder binder) {
        binder.onRegister(this, webConnection);
    }

    public abstract void set(String key, Object value);
    public abstract Object get(String key);

    public abstract void render();
}
