package org.alexdev.icarus.web.util.config;

import org.alexdev.icarus.web.util.response.WebResponses;

public class Settings {

    private String siteDirectory;
    private String templateDirectory;
    private String templateName;
    private WebResponses webResponses;

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

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public WebResponses getWebResponses() {
        return webResponses;
    }

    public void setWebResponses(WebResponses webResponses) {
        this.webResponses = webResponses;
    }
}
