package org.alexdev.duckhttpd.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil {

    public static byte[] compress(String str) throws Exception {

        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);

        gzip.write(str.getBytes("UTF-8"));
        gzip.close();

        return obj.toByteArray();
    }

    public static String decompress(byte[] bytes) throws Exception {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        return IOUtils.toString(gis, Charset.forName("UTF-8"));
    }
}
