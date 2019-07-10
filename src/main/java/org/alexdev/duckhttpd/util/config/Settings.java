package org.alexdev.duckhttpd.util.config;

import org.alexdev.duckhttpd.template.DefaultTemplate;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.response.DefaultWebResponse;
import org.alexdev.duckhttpd.response.WebResponses;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Settings {
    private int cacheRenewTime;
    private String siteDirectory;
    private WebResponses defaultResponses;
    private Class<? extends Template> templateBase;
    private Map<String, String> defaultHeaders;

    private static Settings instance;

    public Settings() {
        this.cacheRenewTime = (int)TimeUnit.DAYS.toSeconds(7);
        this.siteDirectory = "";
        this.defaultHeaders = new HashMap<>();
        this.defaultResponses = new DefaultWebResponse();
        this.templateBase = DefaultTemplate.class;
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }
    public int getCacheRenewTime() {
        return cacheRenewTime;
    }

    public void setCacheRenewTime(int cacheRenewTime) {
        this.cacheRenewTime = cacheRenewTime;
    }

    public String getSiteDirectory() {
        return siteDirectory;
    }

    public void setSiteDirectory(String siteDirectory) {
        this.siteDirectory = siteDirectory;
    }

    public WebResponses getDefaultResponses() {
        return defaultResponses;
    }

    public void setDefaultResponses(WebResponses defaultResponses) {
        this.defaultResponses = defaultResponses;
    }

    /**
     * Get the template base class.
     *
     * @return the template base
     */
    public Class<? extends Template> getTemplateBase() {
        return templateBase;
    }

    /**
     * Sets the template base.
     *
     * @param templateBase the template base
     */
    public void setTemplateBase(Class<? extends Template> templateBase) {
        this.templateBase = templateBase;
    }

    /**
     * Gets and sets default headers when sending defaultResponses back. Applies to ALL defaultResponses.
     *
     * @return the list of default headers to send
     */
    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }
}