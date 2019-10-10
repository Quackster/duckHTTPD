package org.alexdev.duckhttpd.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageRules {
    private class PageRule {
        private String pattern;
        private String redirection;

        public PageRule(String pattern, String redirection) {
            this.pattern = pattern.replace("*", "(.*?)");
            this.redirection = redirection;
        }

        public String getPattern() {
            return pattern;
        }

        public String getRedirection() {
            return redirection;
        }
    }

    private List<PageRule> pageRuleList;
    private List<PageRule> blacklist;

    private static PageRules instance;

    public PageRules() {
        this.pageRuleList = new ArrayList<>();
        this.blacklist = new ArrayList<>();
    }

    public void addRule(String pattern, String redirection) {
        this.pageRuleList.add(new PageRule(pattern, redirection));
    }

    public void addBlacklist(String pattern) {
        this.blacklist.add(new PageRule(pattern, null));
    }

    public PageRule matchesRule(String url) {
        for (PageRule pageRule : this.blacklist) {
            Pattern pattern = Pattern.compile(pageRule.pattern);
            Matcher matcher = pattern.matcher(url);

            if (matcher.matches()) {
                System.out.println("blacklisted");
                return null;
            }
        }

        for (PageRule pageRule : this.pageRuleList) {
            Pattern pattern = Pattern.compile(pageRule.pattern);
            Matcher matcher = pattern.matcher(url);

            if (matcher.matches()) {
                return pageRule;
            }
        }

        return null;
    }

    public String getNewUrl(PageRule pageRule, String url) {
        Pattern pattern = Pattern.compile(pageRule.pattern);
        Matcher matcher = pattern.matcher(url);

        List<String> list = new ArrayList<>();

        if (matcher.matches()) {
            for (int i = 1; i < matcher.groupCount() + 1; i++) {
                list.add(matcher.group(i));
            }
        }

        String newUrl = pageRule.redirection;

        int i = 1;
        for (var entry : list) {
            newUrl = newUrl.replace("$" + i, entry);
            i++;
        }

        return newUrl;
    }

    public static PageRules getInstance() {
        if (instance == null) {
            instance = new PageRules();
        }

        return instance;
    }
}
