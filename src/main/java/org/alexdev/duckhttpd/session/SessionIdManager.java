package org.alexdev.duckhttpd.session;

import org.alexdev.duckhttpd.queries.WebSession;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;

import java.sql.Time;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SessionIdManager {

    private static final String HTTPSESSID = "HTTPSESSID";
    private static SessionIdManager instance;

    private Map<String, SessionId> sessionIds;

    public SessionIdManager() {
        this.sessionIds = new ConcurrentHashMap<>();
    }

    public SessionId checkSession(WebConnection client) {

        this.checkExpiry();

        /*if (client.id() != null) {
            System.out.println("LOCATE " + client.id().getFingerprint());
            return client.id();
        }*/
        String cookie = client.cookies().getString(HTTPSESSID, "");

        if (sessionIds.containsKey(cookie)) {
            return sessionIds.get(cookie);

        } else {

            SessionId session = new SessionId(client);
            client.cookies().set(HTTPSESSID, session.getFingerprint());

            this.sessionIds.put(cookie, session);
            return session;
        }
    }

    private void checkExpiry() {
        this.sessionIds.entrySet().removeIf(entry -> WebUtilities.currentTimeSeconds() > entry.getValue().getExpireTime());
    }

    public SessionId locateSessionId(String cookie) {

        SessionId sessionId = this.sessionIds.get(cookie);
        return sessionId;
    }



    public static SessionIdManager getInstance() {

        if (instance == null) {
            instance = new SessionIdManager();
        }

        return instance;
    }
}
