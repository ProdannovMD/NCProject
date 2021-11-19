package com.netcracker.logging.levels;

public enum Level {
    ALL(5),
    DEBUG(4),
    ERROR(3),
    FATAL(2),
    INFO(1),
    OFF(0);

    private final int levelValue;

    Level(int levelValue) {
        this.levelValue = levelValue;
    }

    public int getLevelValue() {
        return levelValue;
    }
}
