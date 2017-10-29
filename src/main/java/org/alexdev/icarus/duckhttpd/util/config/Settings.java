package org.alexdev.icarus.duckhttpd.util.config;

import org.alexdev.icarus.duckhttpd.template.DefaultTemplate;
import org.alexdev.icarus.duckhttpd.template.Template;
import org.alexdev.icarus.duckhttpd.util.response.DefaultWebResponse;
import org.alexdev.icarus.duckhttpd.util.response.WebResponses;

public class Settings {

    private String siteDirectory;
    private String templateDirectory;
    private String templateName;

    private WebResponses webResponses = new DefaultWebResponse();
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


    public Class<? extends Template> getTemplateHook() {
        return templateHook;
    }

    public void setTemplateHook(Class<? extends Template> templateHook) {
        this.templateHook = templateHook;
    }
}