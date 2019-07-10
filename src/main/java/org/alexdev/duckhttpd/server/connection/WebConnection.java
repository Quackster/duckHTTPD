package org.alexdev.duckhttpd.server.connection;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import org.alexdev.duckhttpd.queries.WebQuery;
import org.alexdev.duckhttpd.queries.WebSession;
import org.alexdev.duckhttpd.session.CookieSession;
import org.alexdev.duckhttpd.session.CookieSessionManager;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.util.config.Settings;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.queries.WebCookies;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebConnection {
    private Channel channel;

    private FullHttpRequest httpRequest;
    private FullHttpResponse httpResponse;
    private String requestContent;

    private WebQuery postData;
    private WebQuery getData;
    private WebCookies cookies;

    private WebSession session;
    private CookieSession sessionId;

    private boolean fileSent;
    private String routeRequest;
    private boolean isRequestHandled;

    private List<String> matches;
    private Map<String, String> headers;

    public static final AttributeKey<WebConnection> WEB_CONNECTION = AttributeKey.valueOf("WebConnection");

    public WebConnection(Channel channel, FullHttpRequest httpRequest) {
        var requestContent = httpRequest.content();

        this.channel = channel;
        this.httpRequest = httpRequest;
        this.requestContent = requestContent.toString(StandardCharsets.ISO_8859_1);
        this.getData = new WebQuery(this.httpRequest.uri());
        this.postData = new WebQuery("?" + this.requestContent);
        this.cookies = new WebCookies(this);
        this.fileSent = false;
        this.isRequestHandled = false;
        this.matches = new ArrayList<>();
        this.headers = new HashMap<>();

        requestContent.release();

        try {
            this.httpRequest.release();
        } catch (Exception ex) {

        }
    }

    public void validateSession() {
        this.sessionId = CookieSessionManager.getInstance().getSession(this);
        this.cookies().set(CookieSessionManager.HTTPSESSID, this.sessionId.getFingerprint());

        this.session = this.sessionId.getWebSession();
        this.session.loadSessionData();
    }

    public String getIpAddress() {
        String ipAddress = ((InetSocketAddress)this.channel.remoteAddress()).getAddress().toString().substring(1);

        if (this.httpRequest != null) {
            if (this.httpRequest.headers().contains("HTTP_CF_CONNECTING_IP")) {
                ipAddress = this.httpRequest.headers().get("HTTP_CF_CONNECTING_IP");
            }


            if (this.httpRequest.headers().contains("CF-Connecting-IP")) {
                ipAddress = this.httpRequest.headers().get("CF-Connecting-IP");
            }
        }

        return ipAddress;
    }

    public void redirect(String targetUrl) {
        this.tryDisposeResponse();

        if (this.httpResponse == null) {
            this.httpResponse = ResponseBuilder.create("");
        }

        this.httpResponse.setStatus(HttpResponseStatus.FOUND);
        this.httpResponse.headers().add("Location", targetUrl);//Paths.get(Settings.getInstance().getUrl(), "/", targetUrl);
    }

    private void tryDisposeResponse() {
        if (this.httpResponse != null) {
            if (this.httpResponse.refCnt() > 0) {
                this.httpResponse.release();
            }

            this.httpResponse = null;
        }
    }

    public WebQuery post() {
        return postData;
    }

    public WebQuery get() {
        return getData;
    }

    public WebCookies cookies() {
        return cookies;
    }

    public WebSession session() {
        return this.session;
    }

    public Channel channel() {
        return channel;
    }

    public FullHttpRequest request() {
        return httpRequest;
    }

    public Template template() {
        try {
            return Settings.getInstance().getTemplateBase().getDeclaredConstructor(WebConnection.class).newInstance(this);
        } catch (Exception e) {
            Settings.getInstance().getDefaultResponses().getErrorResponse(this, e);
        }

        return null;
    }

    public Template template(String tplName) {
        Template tpl = this.template();
        tpl.start(tplName);
        return tpl;
    }

    public FullHttpResponse response() {
        return httpResponse;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setResponse(FullHttpResponse httpResponse) {
        this.tryDisposeResponse();
        this.httpResponse = httpResponse;
    }

    public CookieSession id() {
        return sessionId;
    }

    public boolean isFileSent() {
        return fileSent;
    }

    public void setFileSent(boolean fileSent) {
        this.fileSent = fileSent;
    }

    public void setWildcardMatches(List<String> matches) {
        this.matches = matches;
    }

    public List<String> getMatches() {
        return matches;
    }

    public void setRouteRequest(String uri) {
        this.routeRequest = uri;
    }

    public String getRouteRequest() {
        return routeRequest;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public boolean isRequestHandled() {
        return isRequestHandled;
    }

    public void setRequestHandled(boolean requestHandled) {
        isRequestHandled = requestHandled;
    }
}
