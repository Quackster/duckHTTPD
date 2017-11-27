package org.alexdev.duckhttpd.session;

import org.alexdev.duckhttpd.queries.WebSession;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SessionIdManager implements Runnable {

    private static final String HTTPSESSID = "HTTPSESSID";
    private static final int expireTimeMinutes = 24;
    private static SessionIdManager instance;

    private File sessionDirectory;
    private Map<String, SessionId> sessionIds;
    private List<String> cachedSessions;
    private ScheduledExecutorService executorService;

    public SessionIdManager() {
        this.sessionIds = new ConcurrentHashMap<>();
        this.cachedSessions = new ArrayList<>();
        this.createScheduler();

        this.sessionDirectory = new File("session-cookies");

        if (!this.sessionDirectory.exists()) {
            this.sessionDirectory.mkdir();
        }
    }

    /**
     * Creates scheduler to clear stored cookies every 24 hours
     */
    private void createScheduler() {
        this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        this.executorService.scheduleAtFixedRate(this, 0, expireTimeMinutes, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        this.sessionIds.entrySet().removeIf(entry -> WebUtilities.currentTimeSeconds() > entry.getValue().getExpireTime());

        for (File file : sessionDirectory.listFiles()) {

            if (System.currentTimeMillis() > (file.lastModified() + TimeUnit.MINUTES.toMillis(expireTimeMinutes))) {
                try {
                    file.delete();
                } catch (Exception e) { }
                continue;
            }

            this.cachedSessions.add(file.getName());
        }

        for (SessionId id : sessionIds.values()) {
            id.getWebSession().saveSessionData();
        }
    }

    /**
     * Checks if the connection has a stored session cookie, if not, send one.
     * And retrieve the session ID instance.
     *
     * @param client the http connection
     * @return the session id
     */
    public SessionId checkSession(WebConnection client) {

        String cookie = client.cookies().getString(HTTPSESSID, "");

        if (sessionIds.containsKey(cookie) && cookie.length() > 0) {
            return sessionIds.get(cookie);

        } else {

            SessionId session = new SessionId(client);

            if (cachedSessions.contains(cookie)) {
                session.setFingerprint(cookie);
            } else {
                session.generateFingerprint();
            }

            client.cookies().set(HTTPSESSID, session.getFingerprint());

            this.sessionIds.put(cookie, session);
            return session;
        }
    }

    /**
     * Get the session directory instance
     *
     * @return the directory instance
     */
    public File getSessionDirectory() {
        return sessionDirectory;
    }


    public static int getExpireTimeMinutes() {
        return expireTimeMinutes;
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
