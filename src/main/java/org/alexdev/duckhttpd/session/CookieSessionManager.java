package org.alexdev.duckhttpd.session;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.WebUtilities;
import org.alexdev.duckhttpd.util.config.Settings;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CookieSessionManager implements Runnable {
    public static final String HTTPSESSID = "HTTPSESSID";
    public static final long EXPIRE_TIME = TimeUnit.HOURS.toMinutes(24);
    private static CookieSessionManager instance;

    private File sessionDirectory;
    private ScheduledExecutorService executorService;
    private List<CookieSession> cookieSessionList;

    public CookieSessionManager() {
        this.createScheduler();
        this.cookieSessionList = new CopyOnWriteArrayList <>();
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
        this.executorService.scheduleAtFixedRate(this, 0, EXPIRE_TIME, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        try {
            if (Settings.getInstance().isSaveSessions()) {
                if (this.sessionDirectory != null && this.sessionDirectory.listFiles() != null) {
                    for (File file : this.sessionDirectory.listFiles()) {
                        if (file == null) {
                            continue;
                        }

                        if (System.currentTimeMillis() > file.lastModified() + TimeUnit.MINUTES.toMillis(EXPIRE_TIME)) {
                            try {
                                file.delete();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            } else {
                this.cookieSessionList.removeIf(x -> System.currentTimeMillis() > x.getExpireTime());
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Checks if the connection has a stored session cookie, if not, send one.
     * And retrieve the session ID instance.
     *
     * @param client the http connection
     * @return the session id
     */
    public CookieSession getSession(WebConnection client) {
        String cookie = client.cookies().getString(HTTPSESSID, "");

        boolean createCookie = true;
        CookieSession session = null;// = new CookieSession(client);

        if (cookie != null && cookie.length() > 0) {
            if (Settings.getInstance().isSaveSessions()) {
                if (Paths.get(this.sessionDirectory.getAbsolutePath(), cookie).toFile().exists()) {
                    createCookie = false;
                }
            }
            else {
                if (this.cookieSessionList.stream().anyMatch(x -> x.getFingerprint().equals(cookie))) {
                    session = this.cookieSessionList.stream().filter(x -> x.getFingerprint().equals(cookie)).findFirst().orElse(null);
                    createCookie = false;
                }
            }
        }

        if (session == null) {
            session = new CookieSession(client);
        }

        if (createCookie) {
            session.generateFingerprint();
        } else {
            session.setFingerprint(cookie);
        }

        if (!Settings.getInstance().isSaveSessions()) {
            this.cookieSessionList.add(session);
        }

        return session;
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
    public static long getExpireTime() {
        return EXPIRE_TIME;
    }

    /**
     * Gets the session ID manager instance
     *
     * @return the instance
     */
    public static CookieSessionManager getInstance() {
        if (instance == null) {
            instance = new CookieSessionManager();
        }

        return instance;
    }


}
