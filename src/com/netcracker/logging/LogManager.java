package com.netcracker.logging;

import com.netcracker.logging.configuration.Configuration;
import com.netcracker.logging.configuration.parsers.XMLConfigurationParser;
import com.netcracker.logging.loggers.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogManager {
    private static Logger LOGGER = Configuration.DEFAULT_CONFIGURATION.getLogger(LogManager.class.getName());
    private static final String CONFIGURATION_NAME = "log-config.xml";
    private static final Configuration CONFIGURATION;

    public static Logger getLogger(String name) {
        Logger logger = CONFIGURATION.getLogger(name);
        logger.start();
        return logger;
    }

    public static Logger getLogger(Class<?> clazz) {
        String name = clazz.getName();
        return getLogger(name);
    }

    public static Logger getLogger(String prefix,  Class<?> clazz) {
        String name = prefix + "." + clazz.getName();
        return getLogger(name);
    }

    static {
        LOGGER.start();
        LOGGER.info("Starting configuration");
        Path root = Paths.get(".");
        Path configFile = root.resolve(CONFIGURATION_NAME);

        if (!Files.exists(configFile)) {
            configFile = root.resolve("resources").resolve(CONFIGURATION_NAME);
        }
        if (!Files.exists(configFile)) {
            LOGGER.warn("Cannot find configuration file. Using default configuration");
            CONFIGURATION = Configuration.DEFAULT_CONFIGURATION;
        } else {
            String s = configFile.toString();
            LOGGER.info("Using configuration file: " + configFile);
            Configuration configuration = XMLConfigurationParser.parse(configFile);
            if (configuration == null) {
                LOGGER.warn("Could not parse configuration file. Using default configuration");
                configuration = Configuration.DEFAULT_CONFIGURATION;
            }
            CONFIGURATION = configuration;
        }

        LOGGER.info("Ending configuration");
        LOGGER = CONFIGURATION.getLogger(LogManager.class.getName());
        LOGGER.start();
    }
}
