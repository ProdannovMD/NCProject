package com.netcracker.logging.handlers.layouts;

import com.netcracker.logging.levels.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HTMLLayout {
    private static final String PATTERN = "<tr>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "    </tr>";
    public static final HTMLLayout DEFAULT = new HTMLLayout("Application logs", null);
    private final String title;
    private DateTimeFormatter formatter;

    public HTMLLayout(String title, String dateTimePattern) {
        this.title = title;
        if (dateTimePattern == null)
            formatter = DateTimeFormatter.ISO_DATE_TIME;
        else
            try {
                formatter = DateTimeFormatter.ofPattern(dateTimePattern);
            } catch (IllegalArgumentException e) {
                System.err.println("Bad date time format " + e.getMessage());
                formatter = DateTimeFormatter.ISO_DATE_TIME;
            }
    }

    public String apply(Level level, String message, String name, Throwable throwable) {
        LocalDateTime dateTime = LocalDateTime.now();
        String dateTimeString = dateTime.format(formatter);
        String threadName = Thread.currentThread().getName();
        String levelName = level == null ? "" : level.name();
        message = message == null ? "" : message;
        name = name == null ? "" : name;
        String throwableFirstLine = "";
        if (throwable != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            throwableFirstLine = stringWriter.toString().split("\n")[0];
        }
        return String.format(PATTERN, dateTimeString, threadName, levelName, name, message, throwableFirstLine);
    }

    public String getTitle() {
        return title;
    }
}
