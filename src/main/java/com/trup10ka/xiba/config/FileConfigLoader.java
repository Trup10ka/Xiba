package com.trup10ka.xiba.config;


import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.NoFormatFoundException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.trup10ka.xiba.util.FileUtil.copyFileIfNotExists;

/**
 * This class was reused from the previous project Kappa
 * @see <a href="https://github.com/Trup10ka/Kappa/blob/main/src/main/java/com/trup10ka/kappa/config/FileConfigLoader.java">Kappa - File config loader</a>
 */
public class FileConfigLoader implements ConfigLoader
{
    private final Logger logger = LoggerFactory.getLogger(FileConfigLoader.class);

    private final String[] requiredFields = { "host", "port" };

    private final String filePath;

    public FileConfigLoader(String filePath)
    {
        this.filePath = filePath;
    }

    @Override
    @NotNull
    public XibaConfig loadConfig()
    {
        copyFileIfNotExists("config.conf", "config.conf");
        try (FileConfig fileConfig = FileConfig.of(filePath))
        {
            fileConfig.load();
            checkIfAnyFiledInConfigIsMissing(fileConfig);

            logger.info("Config file loaded successfully");
            return new XibaConfig(
                    fileConfig.get("host"),
                    fileConfig.get("port"),
                    new XibaConfig.Timeouts(
                            fileConfig.get("timeouts.client"),
                            fileConfig.get("timeouts.proxy-client")
                    ),
                    new XibaConfig.Ranges(
                            fileConfig.get("ranges.min-account-number"),
                            fileConfig.get("ranges.max-account-number"),
                            fileConfig.get("ranges.min-port"),
                            fileConfig.get("ranges.max-port")
                    ),
                    new XibaConfig.BankRobbery(
                            fileConfig.get("robbery-plan.subnet-mask"),
                            fileConfig.get("robbery-plan.max-pool-size"),
                            fileConfig.get("robbery-plan.command-timeout")
                    )
            );
        }
        catch (NullPointerException e)
        {
            logger.error("You are missing a value in configuration file, missing field: {}", e.getMessage());
            System.exit(1);
            return null;
        }
        catch (NoFormatFoundException e)
        {
            copyFileIfNotExists("config.conf", "config.conf");
            System.exit(1);
            return null;
        }
    }

    private void checkIfAnyFiledInConfigIsMissing(FileConfig fileConfig)
    {
        for (String requiredField : requiredFields)
        {
            if (!fileConfig.contains(requiredField))
            {
                logger.error("Missing required field in the config file: {}", requiredField);
                System.exit(1);
            }
        }
    }
}
