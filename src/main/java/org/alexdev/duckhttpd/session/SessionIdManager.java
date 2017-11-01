package org.alexdev.duckhttpd.session;

import org.alexdev.duckhttpd.queries.WebSession;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;

import java.sql.Time;
import java.util.Map;
import java.util.concurrent.*;

public class SessionIdManager implements Runnable {

    private static final String HTTPSESSID = "HTTPSESSID";
    private static SessionIdManager instance;

    private Map<String, SessionId> sessionIds;
    private ScheduledExecutorService executorService;

    public SessionIdManager() {
        this.sessionIds = new ConcurrentHashMap<>();
        this.createScheduler();
    }

    /**
     * Creates scheduler to clear stored cookies every 24 hours
     */
    private void createScheduler() {
        this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        this.executorService.scheduleAtFixedRate(this, 0, 24, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        this.sessionIds.entrySet().removeIf(entry -> WebUtilities.currentTimeSeconds() > entry.getValue().getExpireTime());
    }

    /**
     * Checks if the connection has a stored session cookie, if not, send one.
     * And retrieve the session ID instance.
     *
     * @param client the web connection
     * @return the session id
     */
    public SessionId checkSession(WebConnection client) {

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

    /**
     * Gets the session ID manager instance
     *
     * @return the instance
     */
    public static SessionIdManager getInstance() {

        if (instance == null) {
            instance = new SessionIdManager();
        }

        return instance;
    }


}
