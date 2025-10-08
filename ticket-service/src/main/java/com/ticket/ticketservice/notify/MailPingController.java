package com.ticket.ticketservice.notify;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class MailPingController {
    private final EmailService email;
    private final PdfService pdf;

    @GetMapping("/dev/mail/text")
    public String text(@RequestParam String to){
        email.sendText(to, "Ping", "Mail OK " + new Date());
        return "sent";
    }

    @GetMapping("/dev/mail/pdf")
    public String pdf(@RequestParam String to) throws MessagingException {
        byte[] bytes = pdf.ticketSummary(1L,"Demo","RESOLVED","MEDIUM","IT",
                "tester@me.com","ok",new Date(),new Date());
        email.sendWithAttachment(to, "Ping PDF", "see attachment", bytes, "demo.pdf");
        return "sent";
    }
}
