package org.alexdev.icarus.duckhttpd.server.session;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.alexdev.icarus.duckhttpd.template.Template;
import org.alexdev.icarus.duckhttpd.util.config.Settings;
import org.alexdev.icarus.duckhttpd.util.response.ResponseBuilder;

import java.util.HashMap;
import java.util.Map;

public class WebSession {

    private Channel channel;

    private FullHttpRequest httpRequest;
    private FullHttpResponse httpResponse;

    private WebQuery postData;
    private WebQuery getData;
    private WebQuery sessionData;

    private WebCookies cookies;

    public static final AttributeKey<Map<String, String>> SESSION_DATA = AttributeKey.valueOf("SessionDataMap");

    public WebSession(Channel channel, FullHttpRequest httpRequest) {
        this.channel = channel;
        this.httpRequest = httpRequest;

        if (!this.channel.hasAttr(SESSION_DATA)) {
            this.channel.attr(SESSION_DATA).set(new HashMap<>());
        }

        this.getData = new WebQuery(this.httpRequest.uri());
        this.postData = new WebQuery("?" + this.httpRequest.content().toString(CharsetUtil.UTF_8));
        this.sessionData = new WebQuery(this.channel.attr(SESSION_DATA).get());
        this.cookies = new WebCookies(this);
    }

    public void redirect(String targetUrl) {

        if (this.httpResponse == null) {
            this.httpResponse = ResponseBuilder.getHtmlResponse("");
        }

        this.httpResponse.setStatus(HttpResponseStatus.FOUND);
        this.httpResponse.headers().add(HttpHeaderNames.LOCATION, targetUrl);
    }

    public WebQuery post() {
        return postData;
    }

    public WebQuery get() {
        return getData;
    }

    public WebQuery session() {
        return sessionData;
    }

    public WebCookies cookies() {
        return cookies;
    }

    public Channel channel() {
        return channel;
    }

    public FullHttpRequest request() {
        return httpRequest;
    }

    public Template template() {
        try {
            return Settings.getInstance().getTemplateHook().getDeclaredConstructor(WebSession.class).newInstance(this);
        } catch (Exception e) {
            Settings.getInstance().getWebResponses().getInternalServerErrorResponse(e);
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
