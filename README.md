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

To manipulate the incoming requests and to decide what should be sent back, you just simply need to register a route shown in the example above. With Java 8 it's possible to use Lambda functions to decide which specific methods in a class will handle each request. Make sure you import **RouteManager** class from the duckHTTPD package.

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

## Error Handling

To be continued...
