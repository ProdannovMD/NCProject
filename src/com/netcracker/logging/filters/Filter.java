package com.netcracker.logging.filters;

import com.netcracker.logging.levels.Level;

public interface Filter {
    boolean  isApplicable(Level level, String message, String name, Throwable throwable);
}
