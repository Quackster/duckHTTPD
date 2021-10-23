package org.alexdev.duckhttpd;

import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.WebServer;
import org.alexdev.duckhttpd.util.config.Settings;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        RouteManager.addRoute("/", client -> {
            if (client.session().getString("test") == null) {
                client.session().set("test", "" + new Random().nextInt(1000));
            }
            client.send("Hello, World! "+ client.getSessionId().getFingerprint() + " - " + client.session().getString("test"));
        });

        Settings.getInstance().setSaveSessions(false);

        try {
            new WebServer(81).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
