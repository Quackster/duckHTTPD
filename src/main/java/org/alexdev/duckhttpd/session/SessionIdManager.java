package org.alexdev.duckhttpd.session;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionIdManager implements Runnable {
    private static final String HTTPSESSID = "HTTPSESSID";
    private static final long expireTimeMinutes = TimeUnit.HOURS.toMinutes(24);
    private static SessionIdManager instance;

    private File sessionDirectory;
    private Map<String, SessionId> sessionIds;
    private List<String> cachedSessions;
    private ScheduledExecutorService executorService;

    public SessionIdManager() {
        this.sessionIds = new ConcurrentHashMap<>();
        this.cachedSessions = new ArrayList<>();
        this.createScheduler();

        this.sessionDirectory = new File("tmp");

        if (!this.sessionDirectory.exists()) {
            this.sessionDirectory.mkdir();
        } else {
            if (this.sessionDirectory.isFile()) {
                this.sessionDirectory.delete();
                this.sessionDirectory.mkdir();
            }
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
        try {
            this.cachedSessions.clear();
            this.sessionIds.entrySet().removeIf(entry -> WebUtilities.currentTimeSeconds() > entry.getValue().getExpireTime());

            if (this.sessionDirectory == null) {
                return;
            }

            if (this.sessionDirectory.listFiles() == null) {
                return;
            }

            for (File file : this.sessionDirectory.listFiles()) {
                if (file == null) {
                    continue;
                }

                if (System.currentTimeMillis() >= (file.lastModified() + TimeUnit.MINUTES.toMillis(expireTimeMinutes))) {
                    try {
                        file.delete();
                    } catch (Exception e) {
                    }

                    continue;
                }

                this.cachedSessions.add(file.getName());
            }
        } catch (Exception ignored) { }
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

        if (this.sessionIds.containsKey(cookie) && cookie.length() > 0) {
            return this.sessionIds.get(cookie);
        } else {
            SessionId session = new SessionId(client);

            if (this.cachedSessions.contains(cookie)) {
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

    /**
     * Gets the time in minutes that the session data should expire
     *
     * @return the expire time in minutes
     */
    public static long getExpireTimeMinutes() {
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
