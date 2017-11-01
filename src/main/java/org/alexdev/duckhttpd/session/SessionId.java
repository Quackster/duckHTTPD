package org.alexdev.duckhttpd.session;

import org.alexdev.duckhttpd.queries.WebSession;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.concurrent.TimeUnit;

public class SessionId {

    private String hostname;
    private String fingerprint;
    private long expireTime;


    private WebSession webSession;

    public SessionId(WebConnection client) {
        this.hostname = client.channel().remoteAddress().toString();
        this.expireTime = WebUtilities.currentTimeSeconds() + TimeUnit.MINUTES.toSeconds(24); // TODO: Configure GC collection time
        this.fingerprint = DigestUtils.sha256Hex(this.hostname + String.valueOf(WebUtilities.currentTimeSeconds()));
        this.webSession = new WebSession();
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public WebSession getWebSession() {
        return webSession;
    }
}
