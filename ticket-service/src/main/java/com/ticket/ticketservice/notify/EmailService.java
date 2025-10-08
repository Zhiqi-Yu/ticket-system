package com.ticket.ticketservice.notify;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mail;

    public void sendText(String to, String subject, String text) {
        var m = new SimpleMailMessage();
        m.setTo(to);
        m.setSubject(subject);
        m.setText(text);
        mail.send(m);
    }

    public void sendWithAttachment(String to, String subject, String text,
                                   byte[] bytes, String filename) throws MessagingException {
        var mime = mail.createMimeMessage();
        var helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, false);
        helper.addAttachment(filename, new ByteArrayResource(bytes));
        mail.send(mime);
    }
}
