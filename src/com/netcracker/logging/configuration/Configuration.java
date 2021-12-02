package com.netcracker.logging.configuration;

import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.filters.impl.LevelFilter;
import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.handlers.impl.ConsoleHandler;
import com.netcracker.logging.loggers.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private static final Handler DEFAULT_HANDLER = new ConsoleHandler();
    private static final Filter DEFAULT_FILTER = new LevelFilter(null, null);
    private final Map<String, Logger> loggers = new HashMap<>();
    private final File ROOT;
    private final Logger ROOT_LOGGER;

    public static final Configuration DEFAULT_CONFIGURATION = new Configuration();

    private Configuration() {
        this(new HashMap<>());
    }

    public Configuration(Map<String, Logger> configuredLoggers) {
        Path rootPath = Paths.get(".");
        Path src = rootPath.resolve("src");
        Path realPath = null;
        try {
            realPath = src.toRealPath(LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            try {
                realPath = rootPath.toRealPath(LinkOption.NOFOLLOW_LINKS);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }

        ROOT = realPath.toFile();
        ROOT_LOGGER = getNewDefaultLogger("ROOT");
        loggers.put("ROOT", ROOT_LOGGER);

        initLoggers(configuredLoggers, ROOT, ROOT_LOGGER);
    }

    public Logger getLogger(String name) {
        if (loggers.containsKey(name))
            return loggers.get(name);

        return getNewDefaultLogger(name);
    }

    private void initLoggers(Map<String, Logger> configuredLoggers, File currentRoot, Logger currentRootLogger) {
        File[] childrenFiles = currentRoot.listFiles();
        if (childrenFiles != null) {
            for (File childrenFile : childrenFiles) {
                String path = childrenFile.getAbsolutePath().substring(ROOT.getAbsolutePath().length() + 1);
                if (childrenFile.isFile()) {
                    int idx = path.lastIndexOf(".");
                    path = path.substring(0, idx);
                }
                path = path.replace("\\", ".");
                Logger childrenLogger;
                if (configuredLoggers.containsKey(path))
                    childrenLogger = configuredLoggers.get(path);
                else
                    childrenLogger = getNewDefaultLogger(path);
                currentRootLogger.addChildrenLogger(childrenLogger);
                loggers.put(path, childrenLogger);

                if (childrenFile.isDirectory()) {
                    initLoggers(configuredLoggers, childrenFile, childrenLogger);
                }
            }
        }
    }

    private static Logger getNewDefaultLogger(String name) {
        return new Logger(
                name,
                new ArrayList<Handler>() {{ add(DEFAULT_HANDLER); }},
                new ArrayList<Filter>() {{ add(DEFAULT_FILTER); }}
        );
    }
}
