package org.alexdev.duckhttpd.routes;

import org.alexdev.duckhttpd.server.connection.WebConnection;

public interface Route {
    void construct(WebConnection client);
    void handleRoute(WebConnection client) throws Exception;
}
