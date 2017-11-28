package org.alexdev.duckhttpd.routes;

import org.alexdev.duckhttpd.server.connection.WebConnection;

import java.util.HashMap;
import java.util.Map;

public class RouteManager {

    private static Map<String, Route> routes;

    static {
        routes = new HashMap<String, Route>();
    }

    public static void addRoute(String uri, Route route) {
        routes.put(uri, route);
    }

    public static Route getRoute(WebConnection conn, String uri) {

        uri = uri.split("\\?")[0]; // remove GET parameters for lookup

        for (Map.Entry<String, Route> set : routes.entrySet()) {

            if (!set.getKey().endsWith("%")) {
                continue;
            }

            String compareRoute = set.getKey().replace("%", "");

            if (uri.startsWith(compareRoute)) {
                conn.setUriRequest(uri.replace(compareRoute, ""));
                return set.getValue();
            }
        }

        conn.setUriRequest(uri);

        if (routes.containsKey(uri)) {
            return routes.get(uri);
        }

        return null;
    }

    public static Map<String, Route> getRoutes() {
        return routes;
    }
}
