package org.alexdev.duckhttpd.queries;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebQuery {
    private Map<String, String> queries;


    public WebQuery(String queryData) {
        this.queries = new HashMap<>();

        QueryStringDecoder decoder = new QueryStringDecoder(queryData);

        for (Map.Entry<String, List<String>> set : decoder.parameters().entrySet()) {
            if (set.getKey().isEmpty()) {
                continue;
            }

            if (set.getValue().isEmpty()) {
                this.queries.put(set.getKey(), null);
            } else {
                this.queries.put(set.getKey(), set.getValue().get(0));
            }
        }
    }

    public String get(String key) {
        if (this.queries.containsKey(key)) {
            return this.queries.get(key);
        }

        return "";
    }

    public boolean getBoolean(String key) {
        if (this.queries.containsKey(key)) {
            return Boolean.valueOf(this.queries.get(key));
        }

        return false;
    }

    public int getInt(String key) {
        if (this.queries.containsKey(key)) {
            return Integer.parseInt(this.queries.get(key).toString());
        }

        return 0;
    }

    public boolean contains(String key) {
        return this.queries.containsKey(key);
    }

    public boolean excluded(String key) {
        return !this.queries.containsKey(key);
    }

    public void set(String key, String value) {
        this.queries.put(key, value);
    }

    public void delete(String key) {
        this.queries.remove(key);
    }

    public Map<String, String> queries() {
        return queries;
    }
}
