package com.netcracker.logging.configuration;

import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.filters.impl.LevelFilter;
import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.handlers.impl.ConsoleHandler;
import com.netcracker.logging.loggers.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private final Handler DEFAULT_HANDLER;
    private final Filter DEFAULT_FILTER;
    private final Logger DEFAULT_LOGGER;
    private final Map<String, Logger> loggers = new HashMap<>();

    public Configuration() {
        DEFAULT_HANDLER = new ConsoleHandler();
        DEFAULT_FILTER = new LevelFilter();

        DEFAULT_LOGGER = new Logger(
                new ArrayList<Handler>() {{ add(DEFAULT_HANDLER); }},
                new ArrayList<Filter>() {{ add(DEFAULT_FILTER); }}
        );
    }

    public Logger getLogger(String name) {
        if (loggers.containsKey(name))
            return loggers.get(name);

        return DEFAULT_LOGGER;
    }
}
