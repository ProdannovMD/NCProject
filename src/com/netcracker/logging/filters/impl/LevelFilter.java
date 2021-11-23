package com.netcracker.logging.filters.impl;

import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.levels.Level;

public class LevelFilter implements Filter {
    private Level level = Level.ERROR;

    public LevelFilter() {
    }

    public LevelFilter(Level level) {
        this.level = level;
    }

    @Override
    public boolean isApplicable(Level level, String message, String name, Throwable throwable) {
        return level.getLevelValue() <= this.level.getLevelValue();
    }
}
