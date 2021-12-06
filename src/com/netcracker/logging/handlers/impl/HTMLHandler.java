package com.netcracker.logging.handlers.impl;

import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.handlers.layouts.HTMLLayout;
import com.netcracker.logging.levels.Level;
import com.netcracker.logging.templates.HTMLTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HTMLHandler implements Handler {
    private final HTMLLayout layout;
    private final Path path;

    public HTMLHandler(String fileName) throws IOException {
        this(fileName, HTMLLayout.DEFAULT);
    }

    public HTMLHandler(String fileName, HTMLLayout layout) throws IOException {
        this.layout = layout;
        this.path = Paths.get(fileName);
        String template = HTMLTemplate.getTemplate(layout.getTitle());

        if (!Files.exists(path))
            Files.createFile(path);

        Files.write(
                path,
                template.getBytes(StandardCharsets.UTF_8)
        );
    }


    @Override
    public void logMessage(Level level, String message, String name, Throwable throwable) {
        String apply = layout.apply(level, message, name, throwable);
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            return;
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            lines.forEach(line -> {
                try {
                    if (line.contains("</table>")) {
                        bufferedWriter.write(apply);
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                } catch (IOException ignored) { }
            });
            bufferedWriter.flush();
        } catch (IOException ignored) { }
    }
}
