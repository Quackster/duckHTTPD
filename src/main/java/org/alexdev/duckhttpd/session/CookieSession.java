package org.alexdev.duckhttpd.session;

import org.alexdev.duckhttpd.queries.WebSession;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;
import org.alexdev.duckhttpd.util.config.Settings;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CookieSession {
    private String fingerprint;
    private long expireTime;

    private WebConnection client;
    private WebSession webSession;

    public CookieSession(WebConnection client) {
        this.client = client;
        this.expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(CookieSessionManager.getExpireTime()); // TODO: Configure GC collection time
        this.webSession = new WebSession(this.client);
    }

    public void generateFingerprint() {
        this.fingerprint = DigestUtils.sha256Hex(UUID.randomUUID() + String.valueOf(WebUtilities.currentTimeSeconds()));
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public File getSessionFile() {
        if (!Settings.getInstance().isSaveSessions())
            return null;

        try {
            return Paths.get(CookieSessionManager.getInstance().getSessionDirectory().getCanonicalPath(), fingerprint).toFile();
        } catch (IOException ignored) {

        }

        return null;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public WebSession getWebSession() {
        return webSession;
    }
}
