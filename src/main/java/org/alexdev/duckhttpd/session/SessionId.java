package org.alexdev.duckhttpd.session;

import org.alexdev.duckhttpd.queries.WebSession;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.concurrent.TimeUnit;

public class SessionId {

    private String id;
    private String fingerprint;
    private long expireTime;

    private WebSession webSession;

    public SessionId(WebConnection client) {
        this.id = client.channel().id().toString();
        this.expireTime = WebUtilities.currentTimeSeconds() + TimeUnit.MINUTES.toSeconds(24); // TODO: Configure GC collection time
        this.fingerprint = DigestUtils.sha256Hex(this.id + String.valueOf(WebUtilities.currentTimeSeconds()));
        this.webSession = new WebSession();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
