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

A simple implementation of the API is found below, please note, this is an extremely basic example and if you wish use more features then I recommend you keep scrolling down.

```java
int port = Integer.parseInt(args[0]);
System.out.println("Starting duckhttpd service on port " + port);

RouteManager.addRoute("/example", new Route() {
    @Override
    public void handleRoute(WebConnection client) throws Exception {
        client.setResponse(ResponseBuilder.create("<h2>Hello, World!</h2>"));
    }
});

WebServer server = new WebServer(port);
try {
    server.start();
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

This server will listen on the specified port given by the arguments, and the URL request http://localhost/example will preview 

# Hello, World!

...*and that's the example finished! (For now anyways)*

### Route Handling

To manipulate the incoming requests and to decide what should be sent back, you just simply need to register a route shown in the example above. With Java 8 it's possible to use Lambda functions to decide which specific methods in a class will handle each request. 

Make sure you import **RouteManager** class from the duckHTTPD package.

**NOTE:** Routes being handled always take first priority over static files and directories in the Site directory setting.

```java
RouteManager.addRoute("/news", SiteController::news);
RouteManager.addRoute("/about", SiteController::about);
```

The SiteController class should look like this below:

```java
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

```java
 Settings settings = Settings.getInstance();
 settings.setResponses(new CustomWebResponses());
```

You should then proceed to implement four methods, the first three being default error handlers where you can find the Exception management for the Internal Server Error handler. 

The last method is not called automatically by the server, this is just a generic method that can be used by the person (you, hopefully) that will use this API.

```java
public class CustomWebResponses implements WebResponses {

    @Override
    public FullHttpResponse getForbiddenResponse(WebConnection client) {
        return null;
    }

    @Override
    public FullHttpResponse getNotFoundResponse(WebConnection client) {
        return null;
    }

    @Override
    public FullHttpResponse getInternalServerErrorResponse(WebConnection client, Throwable cause) {
        return null;
    }

    @Override
    public FullHttpResponse getErrorResponse(WebConnection client, String header, String message) {
        return null;
    }
}
```

If you want to use the default response for one of these methods, simply use the **DefaultWebResponse** class and return the method it gaves, it will never be null.

```java
@Override
public FullHttpResponse getForbiddenResponse(WebConnection client) {
    return new DefaultWebResponse().getForbiddenResponse(client);
}
```

### Static Files

If you want to use files such as CSS and JS files to create the rest of your website, duckHTTPD will take care of that, all that's required is for you to define where that directory is to help locate the files when a HTTP request is sent.

```java
Settings settings = Settings.getInstance();
settings.setSiteDirectory("tools/www");
```

The example below shows that it will look inside the tools/www folder (relative to this server's working directory) and it will return any files over HTTP that's been requested.

If what's been requested is only a directory, the HTTP server will return a 403 Forbidden response, there is absolutely no way for people to look inside directories, or any other directories outside of www/tools.

So lets say you have a CSS file called **example.css** and it's located in ``/tools/www/css/`` then it means that accessing http://localhost/css/example.css will return that CSS file, if it exists, otherwise it will return a 404.

**NOTE:** Routes being handled always take first priority over static files and directories in the Site directory setting.

### Template Hooking

Another feature of duckHTTPD is hooking a template system into the HTTP server. You need to tell **Settings** the template hook class.

```java
Settings settings = Settings.getInstance();
settings.setTemplateHook(ExampleTemplate.class);
```

This class we specified must extend **Template** class from the duckHTTPD package. Your class should by default appear like the example below:

```java
public class ExampleTemplate extends Template {

    public ExampleTemplate(WebConnection webConnection) {
        super(webConnection);
    }

    @Override
    public void start(String templateFile) throws Exception {
        
    }

    @Override
    public void set(String key, Object value) {

    }

    @Override
    public void render() {
        return null;
    }
}
```

The the first parameter in the ``start(String templateFile)`` method is for the template file it's looking for. I recommend for something like below to search for the specific file.

```
File file = Paths.get("www-tpl", view + ".tpl").toFile();
```

Which means it will search in ``www-tpl/<filename>.tpl`` directory. The start() method should load the file data and save it as a field. The ``set(String key, Object value)`` should manipulate its field by the key and set it by value.



The ``render()`` method **must** set the response to the **WebConnection** client by using **ResponseBuilder** on the remaining HTML, like below, when using JTwig for example:

```java
    @Override
    public void render() {
        FullHttpResponse response = ResponseBuilder.create(this.template.render(this.model));
        this.webConnection.setResponse(response);
    }
```

And in your controller it should be handled like below.

```java
    public static void news(WebConnection client) throws Exception {
        Template tpl = client.template("news");
        tpl.set("text", "value here");
        tpl.render();
    }
```

