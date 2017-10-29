package org.alexdev.icarus.duckhttpd.routes;

import org.alexdev.icarus.duckhttpd.server.session.WebSession;

public interface Route {
    void handleRoute(WebSession client) throws Exception;
}
