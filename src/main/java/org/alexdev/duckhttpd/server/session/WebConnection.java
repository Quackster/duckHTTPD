package org.alexdev.duckhttpd.server.session;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.alexdev.duckhttpd.server.session.queries.WebQuery;
import org.alexdev.duckhttpd.server.session.queries.WebSession;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.util.config.Settings;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.session.queries.WebCookies;

public class WebConnection {

    private Channel channel;

    private FullHttpRequest httpRequest;
    private FullHttpResponse httpResponse;

    private WebQuery postData;
    private WebQuery getData;

    private WebCookies cookies;
    private WebSession session;

    public static final AttributeKey<WebConnection> WEB_CONNECTION = AttributeKey.valueOf("WebConnection");
    public static final AttributeKey<WebSession> SESSION_DATA = AttributeKey.valueOf("WebSession");

    public WebConnection(Channel channel, FullHttpRequest httpRequest) {
        this.channel = channel;
        this.httpRequest = httpRequest;

        if (!this.channel.hasAttr(SESSION_DATA)) {
            this.channel.attr(SESSION_DATA).set(new WebSession());
        }

        this.getData = new WebQuery(this.httpRequest.uri());
        this.postData = new WebQuery("?" + this.httpRequest.content().toString(CharsetUtil.UTF_8));
        this.cookies = new WebCookies(this);
        this.session = this.channel.attr(SESSION_DATA).get();
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
        return session;
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

    public Template template(String tplName) throws Exception {
        Template tpl = this.template();

        if (tpl == null) {
            return null;
        }

        tpl.start(tplName);
        return tpl;
    }

    public FullHttpResponse response() {
        return httpResponse;
    }

    public void setResponse(FullHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }
}
