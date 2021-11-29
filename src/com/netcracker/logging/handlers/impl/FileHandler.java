package com.netcracker.logging.handlers.impl;

import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.handlers.layouts.PatternLayout;
import com.netcracker.logging.levels.Level;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileHandler implements Handler {
    private PatternLayout layout;
    private final PrintWriter writer;

    public FileHandler(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists())
            file.createNewFile();
        FileWriter fileWriter = new FileWriter(file, true);
        this.writer = new PrintWriter(fileWriter, true);
        layout = PatternLayout.DEFAULT;
    }

    public FileHandler(String fileName, PatternLayout layout) throws IOException {
        this(fileName);
        this.layout = layout;
    }

    @Override
    public void logMessage(Level level, String message, String name, Throwable throwable) {
        writer.println(layout.apply(level, message, name, throwable));
    }
}
