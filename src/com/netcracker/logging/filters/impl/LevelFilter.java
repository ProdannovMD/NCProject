package com.netcracker.logging.filters.impl;

import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.levels.Level;

public class LevelFilter implements Filter {
    private Level level = Level.ERROR;
    private LevelFilterModes mode = LevelFilterModes.LESS_OR_EQUAL;

    public LevelFilter(Level level, LevelFilterModes mode) {
        if (level != null)
            this.level = level;
        if (mode != null)
            this.mode = mode;
    }

    @Override
    public boolean isApplicable(Level level, String message, String name, Throwable throwable) {
        switch (mode) {
            case EQUAL: return level.getLevelValue() == this.level.getLevelValue();

            case NOT_EQUAL: return level.getLevelValue() != this.level.getLevelValue();

            case GREATER_OR_EQUAL: return level.getLevelValue() >= this.level.getLevelValue();

            default: return level.getLevelValue() <= this.level.getLevelValue();
        }
    }
}
