package org.alexdev.duckhttpd.routes;

import org.alexdev.duckhttpd.server.connection.WebConnection;

public interface Route {
    void handleRoute(WebConnection client) throws Exception;
}
