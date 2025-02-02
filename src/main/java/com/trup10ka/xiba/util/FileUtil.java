package com.trup10ka.xiba.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This class was reused from the previous project Kappa
 * @see <a href="https://github.com/Trup10ka/Kappa/blob/main/src/main/java/com/trup10ka/kappa/util/FileUtil.java">Kappa - File util</a>
 */
public class FileUtil
{
    private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void copyFileIfNotExists(String filePath, String defaultFileName)
    {
        if (Files.exists(Paths.get(filePath)))
            return;

        logger.warn("Config file not found, generating template config");

        copyResourceToFile(filePath, defaultFileName);

        logger.warn("Template config generated, please fill in the values in config.conf, program will now exit");

        System.exit(0);
    }

    public static boolean createParentDirectoriesIfNotExists(File parentDirectory)
    {
        if (!parentDirectory.getParentFile().exists())
        {
            return parentDirectory.mkdirs();
        }
        return false;
    }

    private static void copyResourceToFile(String filePath, String defaultFileName)
    {
        try (InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(defaultFileName))
        {
            if (inputStream == null)
            {
                throw new FileNotFoundException("Default config file not found in resources");
            }
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e)
        {
            logger.error("Failed to generate template config, program is exiting, try running again");
            System.exit(1);
        }
    }
}
