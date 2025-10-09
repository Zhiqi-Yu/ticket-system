package com.ticket.ticketservice.notify;

import com.ticket.ticketservice.entity.Ticket;
import com.ticket.ticketservice.model.ActionRequest;
import jakarta.mail.MessagingException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NotificationAspect {

    private final EmailService email;
    private final PdfService pdf;

    public NotificationAspect(EmailService email, PdfService pdf) {
        this.email = email;
        this.pdf = pdf;
    }

    // accept：notification.contacts.* or notification.inbox.*
    @Value("${notification.contacts.user:${notification.inbox.user:}}")
    private String userInbox;

    @Value("${notification.contacts.manager:${notification.inbox.manager:}}")
    private String managerInbox;

    @Value("${notification.contacts.admin:${notification.inbox.admin:}}")
    private String adminInbox;

    @Value("${notification.send.on-create:true}")
    private boolean sendOnCreate;
    @Value("${notification.send.on-approve:true}")
    private boolean sendOnApprove;
    @Value("${notification.send.on-reject:true}")
    private boolean sendOnReject;
    @Value("${notification.send.on-resolve:true}")
    private boolean sendOnResolve;

    private static String nz(String s){ return s==null? "" : s; }
    private static boolean isBlank(String s){ return s==null || s.isBlank(); }
    private static boolean notBlank(String s){ return !isBlank(s); }

    // ===== create, send to user and manager =====
    @AfterReturning(
            pointcut = "execution(* com.ticket.ticketservice.service.TicketService.create(..))",
            returning = "ticket")
    public void afterCreate(JoinPoint jp, Ticket ticket) {
        if (ticket == null || !sendOnCreate) return;

        String body = """
        --> User 
        Ticket Created

        ID: %s
        Title: %s
        Priority: %s
        Status: %s
        """.formatted(ticket.getId(), nz(ticket.getTitle()), ticket.getPriority(), ticket.getStatus());

        if (notBlank(userInbox)) {
            email.sendText(userInbox, "[Ticket Created] #"+ticket.getId(), body);
        }
        if (notBlank(managerInbox)) {
            email.sendText(managerInbox, "[Ticket Pending Approval] #"+ticket.getId(),
                    """
                    --> Manager 
                    A new ticket needs your approval
          
                    ID: %s
                    Title: %s
                    Priority: %s
                    """.formatted(ticket.getId(), nz(ticket.getTitle()), ticket.getPriority()));
        }
    }

    // ===== approved, send to user =====
    @AfterReturning(
            pointcut = "execution(* com.ticket.ticketservice.service.TicketService.approve(..))",
            returning = "ticket")
    public void afterApprove(JoinPoint jp, Ticket ticket) {
        if (ticket == null || !sendOnApprove || isBlank(userInbox)) return;

        email.sendText(userInbox, "[Ticket Approved] #"+ticket.getId(),
                """
                --> User 
                Your ticket was approved.
        
                ID: %s
                Title: %s
                """.formatted(ticket.getId(), nz(ticket.getTitle())));

        // send to admin after approve
        if (notBlank(adminInbox)) {
            email.sendText(adminInbox,
                    "[Action Required] Resolve Ticket #" + ticket.getId(),
                    """
                    --> Admin
                    Ticket has been approved and is waiting for RESOLVE.
            
                    ID: %s
                    Title: %s
                    Priority: %s
                    """.formatted(ticket.getId(), nz(ticket.getTitle()), ticket.getPriority()));
        }
    }

    // ===== rejected, send to user =====
    @AfterReturning(
            pointcut = "execution(* com.ticket.ticketservice.service.TicketService.reject(..))",
            returning = "ticket")
    public void afterReject(JoinPoint jp, Ticket ticket) {
        if (ticket == null || !sendOnReject || isBlank(userInbox)) return;

        email.sendText(userInbox, "[Ticket Rejected] #"+ticket.getId(),
                """
                -- > User 
                Your ticket was rejected.
        
                ID: %s
                Title: %s
                """.formatted(ticket.getId(), nz(ticket.getTitle())));
    }

    // ===== resolve, send to user and cc to admin =====
    @AfterReturning(
            pointcut = "execution(* com.ticket.ticketservice.service.TicketService.resolve(..))",
            returning = "ticket")
    public void afterResolve(JoinPoint jp, Ticket ticket) throws MessagingException {
        if (ticket == null || !sendOnResolve || isBlank(userInbox)) return;

        String comments = "";
        for (Object a : jp.getArgs()) {
            if (a instanceof ActionRequest ar && ar.comments != null) {
                comments = ar.comments;
                break;
            }
        }
        byte[] bytes = pdf.ticketSummary(
                ticket.getId(), nz(ticket.getTitle()),
                ticket.getStatus().name(), ticket.getPriority().name(),
                ticket.getCategory(), "", comments,
                ticket.getCreatedAt(), ticket.getUpdatedAt()
        );

        email.sendWithAttachment(
                userInbox,
                "[Ticket Resolved] #" + ticket.getId(),
                """
                --> User 
                Your ticket has been resolved.
                
                ID: %s
                Title: %s
                """.formatted(ticket.getId(), nz(ticket.getTitle())),
                bytes,
                "ticket-" + ticket.getId() + ".pdf"
        );

         // cc to admin
         if (notBlank(adminInbox)) {
           email.sendWithAttachment(
               adminInbox,
               "[Ticket Resolved] #" + ticket.getId(),
               "Ticket resolved (cc admin).",
               bytes,
               "ticket-" + ticket.getId() + ".pdf"
           );
         }
    }
}
