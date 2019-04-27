package org.alexdev.duckhttpd.queries;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebQuery {
    private Map<String, List<String>> queries;


    public WebQuery(String queryData) {
        this.queries = new HashMap<>();
        QueryStringDecoder decoder = null;

        try {
            decoder = new QueryStringDecoder(queryData);
        } catch (Exception ex) {

        }

        if (decoder != null) {
            for (Map.Entry<String, List<String>> set : decoder.parameters().entrySet()) {
                if (set.getKey().isEmpty()) {
                    continue;
                }

                if (set.getValue().isEmpty()) {
                    this.queries.put(set.getKey(), null);
                } else {
                    this.queries.put(set.getKey(), set.getValue());
                }
            }
        }
    }

    public List<String> getArray(String key) {
        if (this.queries.containsKey(key)) {
            return this.queries.get(key);
        }

        return new ArrayList<>();
    }

    public String getString(String key) {
        if (this.queries.containsKey(key)) {
            return this.queries.get(key).get(0);
        }

        return "";
    }

    public boolean getBoolean(String key) {
        if (this.queries.containsKey(key)) {
            return Boolean.valueOf(this.queries.get(key).get(0));
        }

        return false;
    }

    public int getInt(String key) {
        if (this.queries.containsKey(key)) {
            return Integer.parseInt(this.queries.get(key).get(0).toString());
        }

        return 0;
    }

    public boolean contains(String key) {
        return this.queries.containsKey(key);
    }

    public boolean excluded(String key) {
        return !this.queries.containsKey(key);
    }

    public void set(String key, List<String> value) {
        this.queries.put(key, value);
    }

    public void setValue(String key, String value) {
        this.queries.put(key, List.of(value));
    }

    public void delete(String key) {
        this.queries.remove(key);
    }

    public Map<String, List<String>> queries() {
        return queries;
    }

    public HashMap<String, String> getValues() {
        var map = new HashMap<String, String>();

        for (var entrySet : this.queries.entrySet()) {
            if (entrySet.getValue() == null) {
                continue;
            }

            if (entrySet.getValue().isEmpty()) {
                continue;
            }

            map.put(entrySet.getKey(), entrySet.getValue().get(0));
        }

        return map;
    }
}
