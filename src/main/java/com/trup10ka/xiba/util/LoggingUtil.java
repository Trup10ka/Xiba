package com.trup10ka.xiba.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

import static com.trup10ka.xiba.util.FileUtil.createParentDirectoriesIfNotExists;

public class LoggingUtil
{
    public static void compressLogFile(String sourceFile, String targetFile) throws IOException
    {
        createParentDirectoriesIfNotExists(Paths.get(targetFile).toFile());

        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile);
             GZIPOutputStream gzipOut = new GZIPOutputStream(fos))
        {
            fis.transferTo(gzipOut);
        }

        Files.deleteIfExists(Paths.get(sourceFile));
    }
}
