package org.alexdev.duckhttpd.server.connection;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.alexdev.duckhttpd.queries.WebQuery;
import org.alexdev.duckhttpd.queries.WebSession;
import org.alexdev.duckhttpd.session.SessionId;
import org.alexdev.duckhttpd.session.SessionIdManager;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.util.config.Settings;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.queries.WebCookies;

import java.net.InetSocketAddress;

public class WebConnection {

    private Channel channel;

    private FullHttpRequest httpRequest;
    private FullHttpResponse httpResponse;
    private String requestContent;

    private WebQuery postData;
    private WebQuery getData;
    private WebCookies cookies;

    private WebSession session;
    private SessionId sessionId;

    private boolean fileResponseOverride;
    private String uriRequest;

    public static final AttributeKey<WebConnection> WEB_CONNECTION = AttributeKey.valueOf("WebConnection");

    public WebConnection(Channel channel, FullHttpRequest httpRequest) {
        this.channel = channel;
        this.httpRequest = httpRequest;
        this.requestContent = httpRequest.content().toString(CharsetUtil.UTF_8);
        this.getData = new WebQuery(this.httpRequest.uri());
        this.postData = new WebQuery("?" + this.requestContent);
        this.cookies = new WebCookies(this);
        this.fileResponseOverride =  false;
    }

    public void validateSession() {
        this.sessionId = SessionIdManager.getInstance().checkSession(this);
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
        if (this.httpResponse == null) {
            this.httpResponse = ResponseBuilder.create("");
        }

        this.httpResponse.setStatus(HttpResponseStatus.FOUND);
        this.httpResponse.headers().add(HttpHeaderNames.LOCATION, targetUrl);//Paths.get(Settings.getInstance().getUrl(), "/", targetUrl);
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
            return Settings.getInstance().getTemplateHook().getDeclaredConstructor(WebConnection.class).newInstance(this);
        } catch (Exception e) {
            Settings.getInstance().getResponses().getInternalServerErrorResponse(this, e);
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
        if (this.httpResponse != null){
            this.httpResponse.release();
        }

        this.httpResponse = httpResponse;
    }

    public SessionId id() {
        return sessionId;
    }

    public boolean hasFileResponseOverride() {
        return fileResponseOverride;
    }

    public void setFileResponseOverride(boolean fileResponseOverride) {
        this.fileResponseOverride = fileResponseOverride;
    }

    public String getUriRequest() {
        return uriRequest;
    }

    public void setUriRequest(String uriRequest) {
        this.uriRequest = uriRequest;
    }
}
