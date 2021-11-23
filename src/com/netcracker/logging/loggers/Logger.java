package com.netcracker.logging.loggers;

import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.levels.Level;

import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private final String NAME;
    private final List<Handler> HANDLERS;
    private final List<Filter> FILTERS;
    private final List<Logger> CHILDREN_LOGGERS;
    private boolean started;

    public Logger(String name, List<Handler> handlers, List<Filter> filters) {
        this.HANDLERS = handlers;
        this.FILTERS = filters;
        this.CHILDREN_LOGGERS = new ArrayList<>();
        this.NAME = name;
        started = false;
    }

    public void start() {
        started = true;
    }

    public void addChildrenLogger(Logger logger) {
        CHILDREN_LOGGERS.add(logger);
    }

    public void info(String message) {
        logMessage(Level.INFO, message, null);
    }

    public void error(String message) {
        logMessage(Level.ERROR, message, null);
    }

    private boolean isApplicable(Level level, String message, Throwable throwable) {
        for (Filter filter : FILTERS) {
            if (!filter.isApplicable(level, message, NAME, throwable))
                return false;
        }
        return true;
    }

    private void logMessage(Level level, String message, Throwable throwable) {
        if (isApplicable(level, message, throwable)) {
            if (started) {
                HANDLERS.forEach(handler -> handler.logMessage(level, message, NAME, throwable));
            }
            CHILDREN_LOGGERS.forEach(logger -> logger.logMessage(level, message, throwable));
        }
    }
}
