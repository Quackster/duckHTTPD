package org.alexdev.duckhttpd.queries;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.CompressionUtil;
import org.alexdev.duckhttpd.util.config.Settings;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebSession {
    private static final Gson gson = new Gson();

    private WebConnection client;
    private ConcurrentMap<String, Object> attributes;

    public WebSession(WebConnection client) {
        this.client = client;
        this.attributes = new ConcurrentHashMap<>();
    }

    public void loadSessionData() {
        try {
            if (!this.client.id().getSessionFile().exists()) {
                this.attributes = new ConcurrentHashMap<>();
                return;
            }

            RandomAccessFile file = new RandomAccessFile(this.client.id().getSessionFile(), "r");

            byte[] fileData = new byte[(int) file.length()];
            file.readFully(fileData);

            if (fileData.length > 0) {
                Type type = new TypeToken<ConcurrentMap<String, Object>>() {}.getType();
                ConcurrentMap<String, Object> tmp = gson.fromJson(CompressionUtil.decompress(fileData), type);

                if (tmp != null) {
                    this.attributes = tmp;
                }
            }

            file.close();

        } catch (Exception e) {

        } finally {
            if (this.attributes == null) {
                this.attributes = new ConcurrentHashMap<>();
            }
        }
    }

    public void saveSessionData() {
        try {
            FileOutputStream writer = new FileOutputStream(this.client.id().getSessionFile(), false);
            writer.write(CompressionUtil.compress(gson.toJson(this.attributes)));
            writer.close();

        } catch (Exception e) {
            //Settings.getInstance().getResponses().getInternalServerErrorResponse(this.client, e);
        }
    }

    public boolean getBoolean(String key) {
        if (this.attributes.containsKey(key) && this.attributes.get(key) != null) {
            return (boolean)this.attributes.get(key);
        }

        return false;
    }

    public int getInt(String key) {
        if (this.attributes.containsKey(key)) {
            return (int)Double.parseDouble(this.attributes.getOrDefault(key, 0).toString());
        }

        return 0;
    }

    public long getLong(String key) {
        if (this.attributes.containsKey(key)) {
            return Long.parseLong(this.attributes.getOrDefault(key, 0).toString());
        }

        return 0;
    }


    public String getString(String key) {
        if (this.attributes.containsKey(key) && this.attributes.get(key) != null) {
            return String.valueOf(this.attributes.get(key));
        }

        return null;
    }

    public String getStringOrElse(String key, String otherValue) {
        if (this.attributes.containsKey(key)) {
            return this.getString(key);
        }

        return otherValue;
    }

    public int getIntOrElse(String key, int otherValue) {
        if (this.attributes.containsKey(key)) {
            return this.getInt(key);
        }

        return otherValue;
    }

    public long getLongOrElse(String key, long otherValue) {
        if (this.attributes.containsKey(key)) {
            return this.getLong(key);
        }

        return otherValue;
    }

    public <T> T get(String key, Class<T> attributeClass) {
        if (this.attributes.containsKey(key)) {
            Object obj = this.attributes.get(key);

            if (obj.getClass().isAssignableFrom(attributeClass)) {
                return attributeClass.cast(this.attributes.get(key));
            } else {
                this.attributes.remove(key);
            }
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
        this.saveSessionData();
    }

    public void delete(String key) {
        this.attributes.remove(key);
        this.saveSessionData();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
