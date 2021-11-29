package com.netcracker.logging.handlers.layouts;

import com.netcracker.logging.levels.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternLayout {
    private final String pattern;
    public static final PatternLayout DEFAULT = new PatternLayout(
            "%d{HH:mm:ss.SSS} [%t] %level %logger{36} - %msg%throwable%n"
    );

    public PatternLayout(String pattern) {
        this.pattern = pattern;
    }

    public String apply(Level level, String message, String name, Throwable throwable) {
        LocalDateTime dateTime = LocalDateTime.now();
        long nanoTime = System.nanoTime();
        String res = pattern;
        res = applyDateTime(res, dateTime);
        res = applyNanoTime(res, nanoTime);
        res = applyLevel(res, level);
        res = applyLoggerName(res, name);
        res = applyN(res);
        res = applyThrowable(res, throwable);
        res = applyThreadId(res);
        res = applyThreadName(res);
        res = applyMessage(res, message);
        return res;
    }

    private String applyDateTime(String currentMessage, LocalDateTime dateTime) {
        String newMessage = currentMessage;

        String stringPattern = "((%date)|(%d))(\\{([a-zA-Z.:]+)})?";
        Pattern dateTimePattern = Pattern.compile(stringPattern);
        Matcher matcher = dateTimePattern.matcher(currentMessage);
        while (matcher.find()) {
            String format = matcher.group(5);
            String formatted;
            if (format != null) {
                try {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
                    formatted = dateTimeFormatter.format(dateTime);
                } catch (IllegalArgumentException e) {
                    formatted = dateTime.toString();
                }
            } else {
                formatted = dateTime.toString();
            }
            newMessage = newMessage.replaceFirst(stringPattern, formatted);
        }

        return newMessage;
    }

    private String applyNanoTime(String currentMessage, long nanoTime) {
        return currentMessage.replaceAll("(%nano)|(%N)", Long.toString(nanoTime));
    }

    private String applyThreadName(String currentMessage) {
        return currentMessage.replaceAll("(%threadName)|(%thread)|(%tn)|(%t)", Thread.currentThread().getName());
    }

    private String applyThreadId(String currentMessage) {
        return currentMessage.replaceAll(
                "(%threadId)|(%tid)|(%T)",
                String.valueOf(Thread.currentThread().getId())
        );
    }

    private String applyMessage(String currentMessage, String message) {
        if (message == null)
            message = "";
        message = message.replace("\\", "\\\\");
        return currentMessage.replaceAll("(%message)|(%msg)|(%m)", message);
    }

    private String applyLevel(String currentMessage, Level level) {
        String levelString = "";
        if (level != null)
            levelString = level.toString();
        return currentMessage.replaceAll("%level", levelString);
    }

    private String applyLoggerName(String currentMessage, String name) {
        if (name == null)
            name = "";
        String newMessage = currentMessage;
        String stringPattern = "%logger(\\{(\\d+)})?";
        Pattern loggerNamePattern = Pattern.compile(stringPattern);
        Matcher matcher = loggerNamePattern.matcher(currentMessage);
        while (matcher.find()) {
            String stringPrecision = matcher.group(2);
            String formattedName;
            if (stringPrecision == null) {
                formattedName = name;
            } else {
                int precision = Math.abs(Integer.parseInt(stringPrecision));
                String[] split = name.split("\\.");
                if (split.length <= precision) {
                    formattedName = name;
                } else {
                    StringJoiner joiner = new StringJoiner(".");
                    String[] strings = Arrays.copyOfRange(split, split.length - precision, split.length);
                    for (String string : strings) {
                        joiner.add(string);
                    }
                    formattedName = joiner.toString();
                }
            }
            newMessage = newMessage.replaceFirst(stringPattern, formattedName);
        }
        return newMessage;
    }

    private String applyThrowable(String currentMessage, Throwable throwable) {
        String newMessage = currentMessage;
        String stringPattern = "%throwable(\\{(\\d+)})?";
        Pattern throwablePattern = Pattern.compile(stringPattern);
        Matcher matcher = throwablePattern.matcher(currentMessage);
        while (matcher.find()) {
            String stringPrecision = matcher.group(2);
            String formattedThrowable;
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            if (throwable == null) {
                printWriter.println();
            } else {
                throwable.printStackTrace(printWriter);
            }
            String[] split = stringWriter.toString().split("\n");
            int precision;
            if (stringPrecision != null) {
                precision = Math.abs(Integer.parseInt(stringPrecision));
                precision = Math.min(split.length, precision);
            } else {
                precision = split.length;
            }

            StringJoiner joiner = new StringJoiner("\n");
            for (int i = 0; i < precision; i++) {
                joiner.add(split[i]);
            }
            formattedThrowable = joiner.toString();

            newMessage = newMessage.replaceFirst(stringPattern, formattedThrowable);
        }
        return newMessage;
    }

    private String applyN(String currentMessage) {
        return currentMessage.replaceAll("%n", "\n");
    }
}
