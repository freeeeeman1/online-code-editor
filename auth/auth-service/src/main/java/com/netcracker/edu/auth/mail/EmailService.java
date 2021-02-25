package com.netcracker.edu.auth.mail;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static javax.mail.Session.getDefaultInstance;

public class EmailService {

    private static final String FROM_EMAIL = "project.nc.edu@yandex.ru";
    private static final String PASSWORD = "nc.edu.project";

    private final Properties props;

    public EmailService() {
        this.props = new Properties();
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }

    public void send(String text, String toEmail) throws MessagingException {
        Session session = getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Welcome to Project");
        message.setText(text);

        Transport.send(message);
    }
}
