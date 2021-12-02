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
    private LoggerFiltersModes mode = LoggerFiltersModes.AND;
    private boolean started;

    public Logger(String name, List<Handler> handlers, List<Filter> filters) {
        this.HANDLERS = handlers;
        this.FILTERS = filters;
        this.CHILDREN_LOGGERS = new ArrayList<>();
        this.NAME = name;
        started = false;
    }

    public Logger(String name, List<Handler> handlers, List<Filter> filters, LoggerFiltersModes mode) {
        this(name, handlers, filters);
        this.mode = mode;
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

    public void info(Throwable throwable) {
        logMessage(Level.INFO, null, throwable);
    }

    public void warn(String message) {
        logMessage(Level.WARN, message, null);
    }

    public void warn(Throwable throwable) {
        logMessage(Level.WARN, null, throwable);
    }

    public void fatal(String message) {
        logMessage(Level.FATAL, message, null);
    }

    public void fatal(Throwable throwable) {
        logMessage(Level.FATAL, null, throwable);
    }

    public void error(String message) {
        logMessage(Level.ERROR, message, null);
    }

    public void error(Throwable throwable) {
        logMessage(Level.ERROR, null, throwable);
    }

    public void debug(String message) {
        logMessage(Level.DEBUG, message, null);
    }

    public void debug(Throwable throwable) {
        logMessage(Level.DEBUG, null, throwable);
    }

    public void trace(String message) {
        logMessage(Level.TRACE, message, null);
    }

    public void trace(Throwable throwable) {
        logMessage(Level.TRACE, null, throwable);
    }


    private boolean isApplicable(Level level, String message, Throwable throwable) {
        if (mode == LoggerFiltersModes.AND) {
            for (Filter filter : FILTERS) {
                if (!filter.isApplicable(level, message, NAME, throwable))
                    return false;
            }
            return true;
        } else {
            for (Filter filter : FILTERS) {
                if (filter.isApplicable(level, message, NAME, throwable))
                    return true;
            }
            return false;
        }
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
