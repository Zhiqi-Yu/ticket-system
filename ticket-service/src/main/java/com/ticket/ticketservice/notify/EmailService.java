package com.ticket.ticketservice.notify;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mail;
    private final String from;

    public EmailService(JavaMailSender mail, @Value("${spring.mail.username}") String from) {
        this.mail = mail;
        this.from = from;
    }

    public void sendText(String to, String subject, String text) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(from);
        m.setTo(to);
        m.setSubject(subject);
        m.setText(text);
        mail.send(m);
    }

    public void sendWithAttachment(String to, String subject, String text,
                                   byte[] bytes, String filename) throws MessagingException {
        var msg = mail.createMimeMessage();
        var helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, false);
        helper.addAttachment(filename, new ByteArrayResource(bytes));
        mail.send(msg);
    }
}
