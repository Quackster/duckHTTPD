package org.alexdev.duckhttpd.routes;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;

import java.util.*;

public class RouteManager {
    private static TreeMap<String, Route> routes;

    static {
        routes = new TreeMap<>(
                (s1, s2) -> {
                    if (s1.length() < s2.length()) {
                        return -1;
                    } else if (s1.length() > s2.length()) {
                        return 1;
                    } else {
                        return s1.compareTo(s2);
                    }
                });
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

        conn.setRouteRequest(uri);
        conn.setRequestHandled(false);

        Route route = null;

        if (routes.containsKey(uri)) {
            return routes.get(uri);
        }

        for (Map.Entry<String, Route> set : routes.entrySet()) {
            String routePath = set.getKey();

            if (routePath.contains("*")) {
                String baseUri = routePath.substring(0, routePath.indexOf("*"));

                if (!uri.startsWith(baseUri)) {
                    continue;
                }
            }

            if (routePath.contains("*")) {
                var matches = WebUtilities.getWildcardEntries(routePath, uri);

                if (matches.size() > 0) {
                    conn.setWildcardMatches(matches);
                    route = set.getValue();
                }
            }
        }

        if (route != null) {
            conn.setRequestHandled(true);
        }

        return route;
    }

    public static Map<String, Route> getRoutes() {
        return routes;
    }
}
