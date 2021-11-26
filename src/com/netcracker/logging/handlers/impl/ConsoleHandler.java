package com.netcracker.logging.handlers.impl;

import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.handlers.layouts.PatternLayout;
import com.netcracker.logging.levels.Level;

public class ConsoleHandler implements Handler {
    private final PatternLayout layout;

    public ConsoleHandler() {
        layout = PatternLayout.DEFAULT;
    }

    public ConsoleHandler(PatternLayout layout) {
        this.layout = layout;
    }

    @Override
    public void logMessage(Level level, String message, String name, Throwable throwable) {
        System.out.print(layout.apply(level, message, name, throwable));
    }
}
