package com.netcracker.project;

import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;

public class MyClass {
    private static final Logger LOGGER = LogManager.getLogger(MyClass.class);

    public static void main(String[] args) {
        LOGGER.info("Test");
    }
}
