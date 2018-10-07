package org.alexdev.duckhttpd.template;

import org.alexdev.duckhttpd.server.connection.WebConnection;

public interface TemplateBinder {
    void onRegister(WebConnection connection);
}
