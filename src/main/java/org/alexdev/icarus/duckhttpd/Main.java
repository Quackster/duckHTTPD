package org.alexdev.icarus.duckhttpd;

import org.alexdev.icarus.duckhttpd.routes.RouteManager;
import org.alexdev.icarus.duckhttpd.server.WebServer;
import org.alexdev.icarus.duckhttpd.util.config.Settings;
import org.alexdev.icarus.duckhttpd.util.response.DefaultWebResponse;

class Main {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("No arguments found, defaulting to port 80 for duckhttpd server.");
            args = new String[] { "80"};
        }

        System.out.println("Registered " + RouteManager.getRoutes().size() + " route(s)!");

        Settings settings = Settings.getInstance();
        settings.setSiteDirectory("tools/www");
        settings.setTemplateDirectory("tools/www-tpl");
        settings.setTemplateName("default");
        settings.setWebResponses(new DefaultWebResponse());

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
