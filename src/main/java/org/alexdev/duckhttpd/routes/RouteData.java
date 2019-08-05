package org.alexdev.duckhttpd.routes;

public class RouteData {
    private String host;
    private String uri;
    private Route route;

    public RouteData(String host, String uri, Route route) {
        this.host = host;
        this.uri = uri;
        this.route = route;
    }

    public String getHost() {
        return host;
    }

    public String getUri() {
        return uri;
    }

    public Route getRoute() {
        return route;
    }
}
