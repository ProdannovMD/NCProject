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

    public static void main(String[] args) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.mailtrap.io");
        prop.put("mail.smtp.port", "2525");
        prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
        prop.put("mail.user", "9037aa6f74e463");
        prop.put("mail.password", "f9ae46d93c0655");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(prop.getProperty("mail.user"), prop.getProperty("mail.password"));
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("nclogger@mail.ru"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("prodanovmd@gmail.com"));
        message.setSubject("Mail Subject");

        String msg = "This is my first email using JavaMailer 2";

        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        message.setContent(multipart);

        Transport.send(message);
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
