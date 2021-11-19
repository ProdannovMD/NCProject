package com.netcracker.logging.loggers;

import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.handlers.Handler;

import java.util.List;

public class Logger {
    private final List<Handler> handlers;
    private final List<Filter> filters;

    public Logger(List<Handler> handlers, List<Filter> filters) {
        this.handlers = handlers;
        this.filters = filters;
    }


}
