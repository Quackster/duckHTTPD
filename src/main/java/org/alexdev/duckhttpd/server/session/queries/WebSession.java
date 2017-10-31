package org.alexdev.duckhttpd.server.session.queries;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSession {

    private Map<String, Object> attributes;

    public WebSession() {
        this.attributes = new ConcurrentHashMap<>();
    }


    public boolean getBoolean(String key) {
        if (this.attributes.containsKey(key)) {
            return (boolean)this.attributes.get(key);
        }

        return false;
    }

    public int getInt(String key) {
        if (this.attributes.containsKey(key)) {
            return Integer.parseInt(this.attributes.get(key).toString());
        }

        return 0;
    }

    public String getString(String key) {
        if (this.attributes.containsKey(key)) {
            return String.valueOf(this.attributes.get(key));
        }

        return null;
    }

    public <T> T get(String key, Class<T> attributeClass) {
        if (this.attributes.containsKey(key)) {
            return attributeClass.cast(this.attributes.get(key));
        }

        return null;
    }

    public boolean contains(String key) {
        return this.attributes.containsKey(key);
    }

    public void set(String key, Object value) {
        this.attributes.put(key, value);
    }

    public void delete(String key) {
        this.attributes.remove(key);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
