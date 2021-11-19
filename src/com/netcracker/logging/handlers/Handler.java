package com.netcracker.logging.handlers;

import com.netcracker.logging.levels.Level;

public interface Handler {
    void logMessage(Level level, String message, String name, Throwable throwable);
}
