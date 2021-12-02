package com.netcracker.logging.handlers.impl;

import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.handlers.layouts.PatternLayout;
import com.netcracker.logging.levels.Level;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailHandler implements Handler {
    private PatternLayout layout;
    private final Properties properties;
    private final Session session;

    public MailHandler(Properties properties) {
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        properties.getProperty("mail.smtp.user"),
                        properties.getProperty("mail.smtp.password")
                );
            }
        });
        this.properties = properties;
        layout = PatternLayout.DEFAULT;
    }

    public MailHandler(
            Properties properties, PatternLayout layout
    ) {
        this(properties);
        this.layout = layout;
    }

    @Override
    public void logMessage(Level level, String message, String name, Throwable throwable) {
        String content = layout.apply(level, message, name, throwable);
        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(properties.getProperty("mail.smtp.from")));
            emailMessage.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(properties.getProperty("mail.smtp.to")));
            emailMessage.setSubject(properties.getProperty("mail.smtp.subject"));

            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(content, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            emailMessage.setContent(multipart);

            Transport.send(emailMessage);
        } catch (MessagingException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            System.err.println("Failed to send logs to email. " + msg);
        } catch (NullPointerException e) {
            System.err.println("Failed to send logs to email. Missing properties.");
        }
    }
}
