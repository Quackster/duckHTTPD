package org.alexdev.duckhttpd.queries;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.CompressionUtil;
import org.alexdev.duckhttpd.util.config.Settings;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSession {

    private static final Gson gson = new Gson();

    private WebConnection client;
    private Map<String, Object> attributes;

    public WebSession(WebConnection client) {
        this.client = client;
        this.attributes = new ConcurrentHashMap<>();
    }

    public void loadSessionData() {

        try {

            if (!this.client.id().getSessionFile().exists()) {
                this.attributes.clear();
                return;
            }

            RandomAccessFile file = new RandomAccessFile(this.client.id().getSessionFile(), "r");

            byte[] fileData = new byte[(int) file.length()];
            file.readFully(fileData);

            if (fileData.length > 0) {

                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> tmp = gson.fromJson(CompressionUtil.decompress(fileData), type);

                if (tmp != null) {
                    this.attributes = tmp;
                }
            }

            file.close();

        } catch (Exception e) {
            Settings.getInstance().getResponses().getInternalServerErrorResponse(this.client, e);
        }
    }

    public void saveSessionData() {

        try {

            FileOutputStream writer = new FileOutputStream(this.client.id().getSessionFile(), false);
            writer.write(CompressionUtil.compress(gson.toJson(this.attributes)));
            writer.close();

        } catch (Exception e) {
            Settings.getInstance().getResponses().getInternalServerErrorResponse(this.client, e);
        }
    }

    public boolean getBoolean(String key) {
        if (this.attributes.containsKey(key)) {
            return (boolean)this.attributes.get(key);
        }

        return false;
    }

    public int getInt(String key) {
        if (this.attributes.containsKey(key)) {
            return (int)Double.parseDouble(this.attributes.get(key).toString());
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

        if (this.attributes.containsKey(key)) {
            this.attributes.remove(key);
        }

        this.attributes.put(key, value);
        //this.saveSessionData();
    }

    public void delete(String key) {
        this.attributes.remove(key);
        //this.saveSessionData();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
