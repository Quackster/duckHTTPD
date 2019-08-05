package org.alexdev.duckhttpd;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.WebServer;

public class Main {
    public static void main(String[] args) {
        RouteManager.addRoute("/test", client -> {
            System.out.println(client.request().headers().get(HttpHeaderNames.HOST));
            client.send("Hello, World!");
        });

        try {
            new WebServer(8080).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
