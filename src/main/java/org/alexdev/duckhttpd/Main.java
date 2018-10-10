package org.alexdev.duckhttpd;

import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.routes.Route;
import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.WebServer;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.config.Settings;
import org.alexdev.duckhttpd.response.DefaultWebResponse;

class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No arguments found, defaulting to port 80 for duckhttpd server.");
            args = new String[] { "80"};
        }

        System.out.println("Registered " + RouteManager.getRoutes().size() + " route(s)!");

        Settings settings = Settings.getInstance();
        settings.setSiteDirectory("tools/www");
        settings.setResponses(new DefaultWebResponse());

        RouteManager.addRoute("/index", new Route() {
            @Override
            public void handleRoute(WebConnection client) throws Exception {
                client.setResponse(ResponseBuilder.create("<h2>Hello world!</h2>"));
            }
        });

        int port = Integer.parseInt(args[0]);
        System.out.println("Starting duckhttpd service on port " + port);

        WebServer server = new WebServer(port);
        try {
            server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
