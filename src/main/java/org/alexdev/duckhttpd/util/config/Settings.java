package org.alexdev.duckhttpd.util.config;

import org.alexdev.duckhttpd.template.DefaultTemplate;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.response.DefaultWebResponse;
import org.alexdev.duckhttpd.response.WebResponses;

public class Settings {

    private String siteDirectory = "";
    private WebResponses responses = new DefaultWebResponse();
    private Class<? extends Template> templateHook = DefaultTemplate.class;

    private static Settings instance;
    public static Settings getInstance() {

        if (instance == null) {
            instance = new Settings();
        }

        return instance;
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
}