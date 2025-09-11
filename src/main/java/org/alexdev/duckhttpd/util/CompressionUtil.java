package org.alexdev.duckhttpd.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static byte[] compress(String str) throws IOException {
        if (str == null || str.isEmpty()) {
            return new byte[0];
        }

        byte[] input = str.getBytes(UTF8);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(baos)) {

            gzip.write(input);
            // Important: finish to ensure all data is written to the BAOS
            gzip.finish();
            return baos.toByteArray();
        }
    }

    public static String decompress(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             GZIPInputStream gzip = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int read;
            while ((read = gzip.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return new String(baos.toByteArray(), UTF8);
        }
    }
}
