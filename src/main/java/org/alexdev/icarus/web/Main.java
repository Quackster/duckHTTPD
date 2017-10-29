package org.alexdev.icarus.web;

import org.alexdev.icarus.web.routes.manager.RouteManager;
import org.alexdev.icarus.web.server.WebServer;
import org.alexdev.icarus.web.util.config.Settings;
import org.alexdev.icarus.web.util.response.DefaultWebResponse;

class Main {
    private static WebServer instance;

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("No arguments found, defaulting to port 80 for web server.");
            args = new String[] { "80"};
        }

        int port = Integer.parseInt(args[0]);

        System.out.println("Starting web service on port " + port);
        System.out.println("Registered " + RouteManager.getRoutes().size() + " route(s)!");

        Settings settings = Settings.getInstance();
        settings.setSiteDirectory("tools/www");
        settings.setTemplateDirectory("tools/www-tpl");
        settings.setTemplateName("default");
        settings.setWebResponses(new DefaultWebResponse());

        WebServer server = new WebServer(port);
        server.start();
    }
}
