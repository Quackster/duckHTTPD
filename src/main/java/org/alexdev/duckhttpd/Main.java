package org.alexdev.duckhttpd;

import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.WebServer;
import org.alexdev.duckhttpd.util.config.Settings;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        /*String test = "(.*?)://(.*?)test";
        String toReplace = "https://$2test";

        Pattern pattern = Pattern.compile(test);

        // get a matcher object from pattern
        Matcher matcher = pattern.matcher("https://bigtest");

        List<String> list = new ArrayList<>();

        if (matcher.matches()) {
            for (int i = 1; i < matcher.groupCount() + 1; i++) {
                list.add(matcher.group(i));
            }
        }

        int i = 1;
        for (var entry : list) {
            toReplace = toReplace.replace("$" + i, entry);
            i++;
        }

        System.out.println(toReplace);*/

        /*PageRules.getInstance().addBlacklist("*://classichabbo.com/block*");
        PageRules.getInstance().addRule("*://classichabbo.com/*", "https://classichabbo.com/$2");

        System.out.println("test 1");
        String url = "http://classichabbo.com/test";

        var matches = PageRules.getInstance().matchesRule(url);

        if (matches != null) {
            System.out.println(PageRules.getInstance().getNewUrl(matches, url));
        }

        System.out.println("test 2");
        url = "://classichabbo.com/block/432432";
        matches = PageRules.getInstance().matchesRule(url);

        if (matches != null) {
            System.out.println(PageRules.getInstance().getNewUrl(matches, url));
        }*/

        /*RouteManager.addRoute("/test", client -> {
            System.out.println(client.request().headers().get(HttpHeaderNames.HOST));
            client.send("Hello, World!");
        });*/

        RouteManager.addRoute("/test", client -> {
            if (client.session().getString("test") == null) {
                client.session().set("test", "" + new Random().nextInt(1000));
            }
            client.send("Hello, World! "+ client.getSessionId().getFingerprint() + " - " + client.session().getString("test"));
        });

        Settings.getInstance().setSaveSessions(false);

        try {
            new WebServer(80).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
