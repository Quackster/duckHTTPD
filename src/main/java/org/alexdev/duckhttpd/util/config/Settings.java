package org.alexdev.duckhttpd.util.config;

import io.netty.util.AsciiString;
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

    private WebResponses responses;
    private Class<? extends Template> templateHook;
    private Map<String, String> headerOverrides;

    private static Settings instance;

    public Settings() {
        this.cacheRenewTime = (int)TimeUnit.DAYS.toSeconds(7);
        this.siteDirectory = "";
        this.headerOverrides = new HashMap<>();
        this.responses = new DefaultWebResponse();
        this.templateHook = DefaultTemplate.class;
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

    public WebResponses getResponses() {
        return responses;
    }

    public void setResponses(WebResponses responses) {
        this.responses = responses;
    }

    public Class<? extends Template> getTemplateHook() {
        return templateHook;
    }

    public void setTemplateHook(Class<? extends Template> templateHook) {
        this.templateHook = templateHook;
    }

    public Map<String, String> getHeaderOverrides() {
        return headerOverrides;
    }
}