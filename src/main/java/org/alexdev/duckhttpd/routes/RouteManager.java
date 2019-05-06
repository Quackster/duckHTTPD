package org.alexdev.duckhttpd.routes;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;

import java.util.HashMap;
import java.util.Map;

public class RouteManager {

    private static Map<String, Route> routes;

    static {
        routes = new HashMap<String, Route>();
    }

    public static void addRoute(String[] uriList, Route route) {
        for (String uri : uriList) {
            routes.put(uri, route);
        }
    }

    public static void addRoute(String uri, Route route) {
        routes.put(uri, route);
    }

    public static Route getRoute(WebConnection conn, String uri) {
        //uri = uri.replace("\\\\", "\\"); // replace double quotes with single quotes
        //uri = uri.replace("\\\\", "\\"); // do it again for good measure
        uri = uri.split("\\?")[0]; // remove GET parameters for lookup

        if (routes.containsKey(uri)) {
            return routes.get(uri);
        }

        for (Map.Entry<String, Route> set : routes.entrySet()) {
            String route = set.getKey();

            if (route.contains("*")) {
                var matches = WebUtilities.getWildcardEntries(route, uri);

                if (matches.size() > 0) {
                    conn.setWildcardMatches(matches);
                    return set.getValue();
                }
            }
        }

        return null;
    }

    public static Map<String, Route> getRoutes() {
        return routes;
    }
}
