package org.alexdev.duckhttpd.routes;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

public class RouteManager {
    private static TreeMap<String, RouteData> routes;

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
            routes.put(uri, new RouteData("", uri, route));
        }
    }

    public static void addRoute(String uri, Route route) {
        routes.put(uri, new RouteData("", uri, route));
    }

    public static Route getRoute(WebConnection conn, String uri) {
        //uri = uri.replace("\\\\", "\\"); // replace double quotes with single quotes
        //uri = uri.replace("\\\\", "\\"); // do it again for good measure
        uri = uri.split("\\?")[0]; // remove GET parameters for lookup
        conn.setRouteRequest(uri);

        Route route = null;

        if (routes.containsKey(uri)) {
            return routes.get(uri).getRoute();
        }

        for (Map.Entry<String, RouteData> set : routes.entrySet()) {
            String routePath = set.getKey();

            if (routePath.contains("*")) {
                String baseUri = routePath.substring(0, routePath.indexOf("*"));

                if (!uri.startsWith(baseUri)) {
                    continue;
                }
            }

            if (routePath.contains("*")) {
                String regexPattern = routePath;
                String regex = "(.*)";

                if (StringUtils.countMatches(routePath, "*") > 1) {
                    regex = "(.*?)";
                }

                regexPattern = routePath.replace("*", regex);

                if (Pattern.matches(regexPattern, uri)) {
                    var matches = WebUtilities.getWildcardEntries(routePath, uri);

                    if (matches.size() > 0) {
                        conn.setWildcardMatches(matches);
                        route = set.getValue().getRoute();
                    }
                }
            }
        }

        return route;
    }

    public static Map<String, RouteData> getRoutes() {
        return routes;
    }
}
