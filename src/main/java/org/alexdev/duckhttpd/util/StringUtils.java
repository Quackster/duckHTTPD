package org.alexdev.duckhttpd.util;

public class StringUtils {
    /**
     * Counts the number of times {@code syntax} appears in {@code s}.
     */
    public static int countMatches(String syntax, String s) {
        if (syntax == null || syntax.isEmpty() || s == null || s.isEmpty()) {
            return 0;
        }

        int count = 0;
        int idx = 0;
        while ((idx = s.indexOf(syntax, idx)) != -1) {
            count++;
            idx += syntax.length();
        }
        return count;
    }

    /**
     * Returns the file extension of {@code name}, or an empty string if none.
     */
    public static String getFileExtension(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == name.length() - 1) {
            return ""; // no extension
        }

        return name.substring(dotIndex + 1);
    }
}
