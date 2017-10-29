package org.alexdev.icarus.duckhttpd.util;

import org.alexdev.icarus.duckhttpd.util.config.Settings;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebUtilities {

    public static String getMimeType(File file) {
        return MimeType.valueOf(FilenameUtils.getExtension(file.getName())).contentType;
    }

    public static byte[] readFile(String relativePath) {
        File file = Paths.get(Settings.getInstance().getSiteDirectory(), relativePath).toFile();
        return readFile(file);
    }

    public static byte[] readFile(File file) {

        try {
            return Files.readAllBytes(Paths.get(file.getCanonicalPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
