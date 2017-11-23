package org.alexdev.duckhttpd.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebUtilities {

    public static String getMimeType(File file) {
        return MimeType.valueOf(FilenameUtils.getExtension(file.getName())).contentType;
    }

    public static byte[] readFile(File file) throws IOException {
        return Files.readAllBytes(Paths.get(file.getCanonicalPath()));
    }

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
