# duckHTTPD

duckHTTPD is a small scalable HTTP web server API written in Java that uses libraries like Netty, this server can easily be hooked into for customised control of requests and handling. duckHTTPD also supports templating and file requests with the correct MIME type.

### Features

* Scalable non-blocking web server
* Customised route handling
* Template hooking
* Default web page handling
* Ability to serve static files such as pictures, media content, CSS, js, etc!
* Error handling
* POST request handling
* GET request handling
* Cookie handling (set and get cookies)
* Session data handling (keep data persistent for as long as the browser is open, securely and server-side only, simillar to PHP's $_SESSION variable).
* Page redirection
* Internally supports Keep-Alive connections

### Example

A simple implementation of the API is found below, please note, this is an extremely basic example and if you wish to do more than I recommend you keep scrolling down for more information on how to implement more features.

```
int port = Integer.parseInt(args[0]);
System.out.println("Starting duckhttpd service on port " + port);

RouteManager.addRoute("/index", new Route() {
    @Override
    public void handleRoute(WebConnection client) throws Exception {
        client.setResponse(ResponseBuilder.create("<h2>Hello World!</h2>"));
    }
});

WebServer server = new WebServer(port);
try {
    server.start();
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

This server will listen on the specified port given by the arguments, and then http://localhost/me will preview 

# Hello World!

...*and that's the example finished! (For now anyways)*

### Route Handling

To manipulate the incoming requests and to decide what should be sent back, you just simply need to register a route shown in the example above. With Java 8 it's possible to use Lambda functions to decide which specific methods in a class will handle each request. 

Make sure you import **RouteManager** class from the duckHTTPD package.

**NOTE:** Routes being handled always take first priority over static files and directories in the Site directory setting.

```
RouteManager.addRoute("/news", SiteController::news);
RouteManager.addRoute("/about", SiteController::about);
```

The SiteController class should look like this below:

```
public class SiteController {

    public static void news(WebConnection client)  {
        client.setResponse(ResponseBuilder.create("<p>The news page!</p>"));
    }
    
    public static void about(WebConnection client)  {
        client.setResponse(ResponseBuilder.create("<p>The about page!</p>"));
    }
```

### Error Handling

By default, there will be default error pages for HTTP responses, 404 (Not Found), 403 (Forbidden) and 500 (Internal Server Error). You can hook into the error handling class by using the example provided below, by using the Settings.java class provided by the API.

```
 Settings settings = Settings.getInstance();
 settings.setResponses(new CustomWebResponses());
```

You should then proceed to implement four methods, the first three being default error handlers where you can find the Exception management for the Internal Server Error handler. The last method is not called automatically by the server, this is just a generic method that can be used by the person (you, hopefully) that will use this API.

```
public class DefaultWebResponse implements WebResponses {

    @Override
    public FullHttpResponse getForbiddenResponse() {
        return null;
    }

    @Override
    public FullHttpResponse getNotFoundResponse() {
        return null;
    }

    @Override
    public FullHttpResponse getInternalServerErrorResponse(Throwable cause) {
        return null;
    }

    @Override
    public FullHttpResponse getErrorResponse(String header, String message) {
        return null;
    }
}
```

If you want to use the default response for one of these methods, simply use the GenericWebResponse() and return that response it gives (it will never be null).

```
@Override
public FullHttpResponse getForbiddenResponse() {
    return new DefaultWebResponse().getForbiddenResponse();
}
```

### Static Files

If you want to use files such as CSS and JS files to create the rest of your website, duckHTTPD will take care of that, all that's required is for you to define where that directory is to help locate the files when a HTTP request is sent.

```
Settings settings = Settings.getInstance();
settings.setSiteDirectory("tools/www");
```

The example below shows that it will look inside the tools/www folder (relative to this server's working directory) and it will return any files over HTTP that's been requested, if what's been requested is only a directory, the HTTP server will return a 403 Forbidden response, there is absolutely no way for people to look inside directories, or any other directories outside of www/tools.

So lets say you have a CSS file called **example.css** and it's located in /tools/www/css/ then it means that accessing http://localhost/css/example.css will return that CSS file, if it exists, otherwise it will return a 404.



