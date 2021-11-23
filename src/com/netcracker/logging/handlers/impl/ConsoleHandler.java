package com.netcracker.logging.handlers.impl;

import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.levels.Level;

import java.util.Calendar;

public class ConsoleHandler implements Handler {
    @Override
    public void logMessage(Level level, String message, String name, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(Calendar.getInstance().getTime()).append(" ")
                .append(level).append(" ");

        if (name != null && !name.isEmpty())
            sb.append("[").append(name).append("] ");

        if (message != null && !message.isEmpty())
            sb.append(message).append(" ");

        System.out.println(sb);

        if (throwable != null)
            throwable.printStackTrace();

    }
}
