package com.ticket.ticketservice.notify;

import com.ticket.ticketservice.entity.Ticket; // ← 你的 Ticket 就在 entity 包
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor // 若不用 Lombok，就删掉这行并手写构造器
public class NotificationAspect {

    private final EmailService emailService;
    private final PdfService pdfService;

    // ========== 创建后：发纯文本邮件 ==========
    @AfterReturning(
            pointcut = "execution(* com.ticket.ticketservice.service.TicketService.create(..))",
            returning = "ticket"
    )
    public void afterCreate(JoinPoint jp, Ticket ticket) {
        if (ticket == null) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String fallback = (auth != null ? auth.getName() : null);
        // 如果你的 Ticket 有 createdByEmail()，用它；没有就先发给当前登录人
        String to = chooseNonBlank(getCreatedByEmailSafe(ticket), fallback, "you@example.com");

        String subject = "[Ticket Created] #" + ticket.getId();
        String body = "Ticket created: " + safe(ticket.getTitle())
                + " (priority " + ticket.getPriority() + ")";

        emailService.sendText(to, subject, body);
    }

    // ========== 解决后：生成 PDF 并作为附件发送 ==========
    @AfterReturning(
            pointcut = "execution(* com.ticket.ticketservice.service.TicketService.resolve(..))",
            returning = "ticket"
    )
    public void afterResolve(JoinPoint jp, Ticket ticket) throws MessagingException {
        if (ticket == null) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = (auth != null ? auth.getName() : "system@local");
        String comments = ""; // 如果以后你有 resolution note，可以在这里填

        byte[] pdf = pdfService.ticketSummary(
                ticket.getId(),
                safe(ticket.getTitle()),
                ticket.getStatus().name(),
                ticket.getPriority().name(),
                /* category */ "",
                actor,
                comments,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );

        String fallback = (auth != null ? auth.getName() : null);
        String to = chooseNonBlank(getCreatedByEmailSafe(ticket), fallback, "you@example.com");

        emailService.sendWithAttachment(
                to,
                "[Ticket Resolved] #" + ticket.getId(),
                "Your ticket has been resolved. Please see the attached summary.",
                pdf,
                "ticket-" + ticket.getId() + ".pdf"
        );
    }

    // ------- helpers -------
    private static String safe(String s){ return s==null? "" : s; }

    /** 如果你的实体没有 createdByEmail 字段，这里安全返回 null 即可 */
    private static String getCreatedByEmailSafe(Ticket t) {
        try {
            // 如果 Ticket 有 getCreatedByEmail() 就用；没有会抛异常 → 返回 null
            return (String) Ticket.class.getMethod("getCreatedByEmail").invoke(t);
        } catch (Exception ignore) { return null; }
    }

    private static String chooseNonBlank(String... candidates) {
        for (String c : candidates) if (c != null && !c.isBlank()) return c;
        return null;
    }
}
