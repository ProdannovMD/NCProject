package com.netcracker.logging.loggers;

import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.levels.Level;

import java.util.List;

public class Logger {
    private final List<Handler> handlers;
    private final List<Filter> filters;

    public Logger(List<Handler> handlers, List<Filter> filters) {
        this.handlers = handlers;
        this.filters = filters;
    }


    private boolean isApplicable(Level level, String message, String name, Throwable throwable) {
        for (Filter filter : filters) {
            if (!filter.isApplicable(level, message, name, throwable))
                return false;
        }
        return true;
    }

    private void logMessage(Level level, String message, String name, Throwable throwable) {
        handlers.forEach(handler -> handler.logMessage(level, message, name, throwable));
    }
}
