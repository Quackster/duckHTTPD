package org.alexdev.duckhttpd.queries;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.internal.StringUtil;
import org.alexdev.duckhttpd.util.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebQuery {
    private Map<String, List<String>> queries;


    public WebQuery(String queryData) {
        this.queries = new HashMap<>();
        Map<String, List<String>> parameters = null;

        try {
            QueryStringDecoder decoder = new QueryStringDecoder(queryData);
            parameters = decoder.parameters();
        } catch (Exception ex) {

        }

        if (parameters != null) {
            for (Map.Entry<String, List<String>> set : parameters.entrySet()) {
                if (set.getKey().isEmpty()) {
                    continue;
                }

                List<String> temp = new ArrayList<>();

                for (var str : set.getValue()) {
                    temp.add(StringEscapeUtils.unescapeHtml4(str));
                }

                if (set.getValue().isEmpty()) {
                    this.queries.put(set.getKey(), null);
                } else {
                    this.queries.put(set.getKey(), temp);
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

    public long getLong(String key) {
        if (this.queries.containsKey(key)) {
            return Long.parseLong(this.queries.get(key).get(0).toString());
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
