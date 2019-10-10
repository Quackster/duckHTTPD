package org.alexdev.duckhttpd;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.routes.PageRules;
import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.WebServer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
