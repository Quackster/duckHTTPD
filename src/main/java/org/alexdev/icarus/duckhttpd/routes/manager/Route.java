package org.alexdev.icarus.duckhttpd.routes.manager;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.icarus.duckhttpd.server.session.WebSession;

public interface Route {
    FullHttpResponse handleRoute(WebSession client) throws Exception;
}
